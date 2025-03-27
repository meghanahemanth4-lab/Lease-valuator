package com.spinteg.lv.service;

import com.spinteg.lv.ExceptionHandling.DataNotFoundException;
import com.spinteg.lv.ExceptionHandling.DataValidation;
import com.spinteg.lv.controller.ContractTypeController;
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

import java.math.BigDecimal;
import java.rmi.dgc.Lease;
import java.util.List;
import java.util.Optional;

import javax.xml.crypto.Data;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {

    private LeaseContractRepository leaseContractRepository;

    private AmortizationEntryRepository amortizationEntryRepository;

    private ContractTypeRepository contractTypeRepository;

    public ResponseEntity<?> updatePaymentStatusToPaid(PaymentApiDTO paymentApiDTO) {

        LeaseContract leaseContract = leaseContractRepository
                .findByCustomerIdAndContractTypeId(paymentApiDTO.getCustomerNumber(),
                        paymentApiDTO.getContractTypeId());

        // Check if the payment date is valid
        DataValidation.isValidDate(paymentApiDTO.getDate().toString(), "Payment Date");

        if (leaseContract != null) {
            List<AmortizationEntry> amortizationEntries = leaseContract.getAmortizationEntries();
            boolean isPaymentFinished = false;
            for (AmortizationEntry entry : amortizationEntries) {
                if (entry.getDate().equals(paymentApiDTO.getDate())) {
                    if (entry.getLeasePaymentStatus().equals(LeasePaymentStatus.PAID)) {
                        return ResponseEntity.status(400).body("Payment already paid");
                    }

                    if (entry.getLeasePaymentStatus().equals(LeasePaymentStatus.NO_RENT_DAY)
                            || entry.getLeasePaymentStatus().equals(LeasePaymentStatus.NO_RENT_DAY_FUTURE)) {
                        return ResponseEntity.status(400).body("No rent day");
                    }

                    if (checkAmountareEqual(paymentApiDTO.getPaymentAmount().toString(),
                            entry.getInstallment().toString()) == false)
                        return ResponseEntity.status(400).body("Payment amount does not match the installment rent");

                    entry.setLeasePaymentStatus(LeasePaymentStatus.PAID);
                    AmortizationEntry amortizationEntry = amortizationEntryRepository.save(entry);
                    isPaymentFinished = true;
                    break;
                }
            }
            if (!isPaymentFinished) {
                return ResponseEntity.status(400).body("Payment not found");
            }
            return ResponseEntity.ok("Payment status updated successfully");
        } else {
            if (!contractExists(paymentApiDTO.getCustomerNumber())) {
                throw new DataNotFoundException("Customer not found");
            }
            if (!contractTypeExists(paymentApiDTO.getContractTypeId())) {
                throw new DataNotFoundException("Contract Type not found");
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

    private boolean checkAmountareEqual(String paymentAmount, String leaseRent) {
        String amount = DataValidation.isNotNegativeAndZero(paymentAmount, "Payment Amount");
        return Double.valueOf(amount).equals((Double.valueOf(leaseRent.trim())));
    }

    public ResponseEntity<?> updateDownPaymentStatusToPaid(PaymentApiDTO paymentApiDTO) {

        LeaseContract leaseContract = leaseContractRepository
                .findByCustomerIdAndContractTypeId(paymentApiDTO.getCustomerNumber(),
                        paymentApiDTO.getContractTypeId());

        // Check if the payment date is valid
        DataValidation.isValidDate(paymentApiDTO.getDate().toString(), "Payment Date");

        if (leaseContract != null) {

            // Check if the downpayment is already paid
            if (leaseContract.getDownpaymentReceived() != null) {
                return ResponseEntity.status(400).body("Downpayment already paid");
            }

            // Check if the payment amount is valid
            DataValidation.isNotNegativeAndZero(paymentApiDTO.getPaymentAmount().toString(), "Payment Amount");

            // Check if the payment amount is equal to the downpayment
            if (checkAmountareEqual(paymentApiDTO.getPaymentAmount().toString(),
                    String.valueOf(leaseContract.getContractType().getDownpayment())) == false)
                return ResponseEntity.status(400).body("Payment amount does not match the downpayment");

            // Update the downpayment status
            leaseContract.setDownpaymentReceived(BigDecimal.valueOf(paymentApiDTO.getPaymentAmount()));
            leaseContract.setDownpaymentTransDate(paymentApiDTO.getDate());

            LeaseContract contract = leaseContractRepository.save(leaseContract);
            if (contract != null) {
                return ResponseEntity.ok("Payment status updated successfully");
            }

        } else {
            if (!contractExists(paymentApiDTO.getCustomerNumber())) {
                throw new DataNotFoundException("Customer not found");
            }
            if (!contractTypeExists(paymentApiDTO.getContractTypeId())) {
                throw new DataNotFoundException("Contract Type not found");
            }
        }
        return ResponseEntity.status(500).body("Downpayment status update failed");
    }
}