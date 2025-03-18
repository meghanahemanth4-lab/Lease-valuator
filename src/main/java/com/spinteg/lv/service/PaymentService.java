package com.spinteg.lv.service;

import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.LeaseContractRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private LeaseContractRepository leaseContractRepository;

    private AmortizationEntryRepository amortizationEntryRepository;

    private ContractTypeRepository contractTypeRepository;

    public ResponseEntity<String> updatePaymentStatusToPaid(PaymentApiDTO paymentApiDTO) {

        LeaseContract leaseContract = leaseContractRepository
                .findByCustomerIdAndContractTypeId(paymentApiDTO.getCustomerNumber(),
                        paymentApiDTO.getContractTypeId());

        if (leaseContract != null) {
            List<AmortizationEntry> amortizationEntries = leaseContract.getAmortizationEntries();
            for (AmortizationEntry entry : amortizationEntries) {
                if (entry.getDate().equals(paymentApiDTO.getDate())) {
                    if (entry.getLeasePaymentStatus().equals(LeasePaymentStatus.PAID)) {
                        return ResponseEntity.status(400).body("Payment already paid");
                    }
                    entry.setLeasePaymentStatus(LeasePaymentStatus.PAID);
                    amortizationEntryRepository.save(entry);
                }
            }
            return ResponseEntity.ok("Payment status updated successfully");
        } else {
            if (!contractExists(paymentApiDTO.getCustomerNumber())) {
                return ResponseEntity.status(404).body("Contract not found");
            }
            if (!contractTypeExists(paymentApiDTO.getContractTypeId())) {
                return ResponseEntity.status(404).body("Contract Type not found");
            }
        }
        return ResponseEntity.status(500).body("Unexpected error occurred");
    }

    private boolean contractExists(String customerNumber) {
        return !leaseContractRepository.findByCustomerNumber(customerNumber).isEmpty();
    }

    private boolean contractTypeExists(String contractTypeId) {
        return !contractTypeRepository.findByContractTypeId(contractTypeId).isEmpty();
    }
}