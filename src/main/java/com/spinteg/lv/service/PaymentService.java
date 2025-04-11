package com.spinteg.lv.service;

import com.spinteg.lv.exception.DataNotFoundException;
import com.spinteg.lv.exception.DataValidation;
import com.spinteg.lv.dto.DownpaymentDTO;
import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.LeaseContractRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

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

                    if (!checkAmountsAreEqual(paymentApiDTO.getPaymentAmount().toString(),
                            entry.getInstallment().toString()))
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

    private boolean checkAmountsAreEqual(String paymentAmount, String leaseRent) {
        String amount = DataValidation.isNotNegativeAndZero(paymentAmount, "Payment Amount");
        return Double.valueOf(amount).equals((Double.valueOf(leaseRent.trim())));
    }

    public ResponseEntity<?> updateDownPaymentStatusToPaid( DownpaymentDTO downpaymentDTO) {

        LeaseContract leaseContract = leaseContractRepository
                .findByCustomerIdAndContractTypeId(downpaymentDTO.getCustomerNumber(),
                        downpaymentDTO.getContractTypeId());

        // Check if the payment date is valid
        DataValidation.isValidDate(downpaymentDTO.getTransactionDate().toString(), "Payment Date");

        if (leaseContract != null) {

            // Check if the downpayment is already paid
            if (leaseContract.getDownpaymentReceived() != null) {
                return ResponseEntity.status(400).body("Downpayment already paid");
            }

            // Check if the payment amount is valid
            DataValidation.isNotNegativeAndZero(downpaymentDTO.getPaymentAmount().toString(), "Payment Amount");

            // Check if the payment amount is equal to the downpayment
            if (checkAmountsAreEqual(downpaymentDTO.getPaymentAmount().toString(),
                    String.valueOf(leaseContract.getContractType().getDownpayment())) == false)
                return ResponseEntity.status(400).body("Payment amount does not match the downpayment");

            // Update the downpayment status
            leaseContract.setDownpaymentReceived(BigDecimal.valueOf(downpaymentDTO.getPaymentAmount()));
            leaseContract.setDownpaymentTransDate(downpaymentDTO.getTransactionDate());

            LeaseContract contract = leaseContractRepository.save(leaseContract);
            if (contract != null) {
                return ResponseEntity.ok("Payment status updated successfully");
            }

        } else {
            if (!contractExists(downpaymentDTO.getCustomerNumber())) {
                throw new DataNotFoundException("Customer not found");
            }
            if (!contractTypeExists(downpaymentDTO.getContractTypeId())) {
                throw new DataNotFoundException("Contract Type not found");
            }
        }
        return ResponseEntity.status(500).body("Downpayment status update failed");
    }
}