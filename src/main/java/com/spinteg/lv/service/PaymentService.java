package com.spinteg.lv.service;

import com.spinteg.lv.exception.AcceptedException;
import com.spinteg.lv.dto.DownpaymentDTO;
import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.Customer;
import com.spinteg.lv.entity.Downpayment;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.entity.enums.VehicleType;
import com.spinteg.lv.exception.DataNotFoundException;
import com.spinteg.lv.exception.DataValidation;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.CustomerRepository;
import com.spinteg.lv.repository.DownPaymentRepository;
import com.spinteg.lv.repository.LeaseContractRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
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

    private DownPaymentRepository downPaymentRepository;

    private CustomerRepository customerRepository;

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

    private boolean customerExists(String customerNumber) {
        return customerRepository.existsByCustomerNumber(customerNumber);
    }

    private boolean contractTypeExists(String contractTypeId) {
        return !contractTypeRepository.findByContractTypeId(contractTypeId).isEmpty();
    }

    private static boolean checkAmountsAreEqual(String paymentAmount, String leaseRent) {
        String amount = DataValidation.isNotNegativeAndZero(paymentAmount, "Payment Amount");
        return Double.valueOf(amount).equals((Double.valueOf(leaseRent.trim())));
    }

    // It is not used
    public ResponseEntity<?> updateDownPaymentStatusToPaid(DownpaymentDTO downpaymentDTO) {

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

    // It is used
    public ResponseEntity<?> updatePaymentStatustobePaid(DownpaymentDTO downpaymentDTO) {

        LeaseContract leaseContract = leaseContractRepository
                .findByCustomerIdAndContractTypeId(downpaymentDTO.getCustomerNumber(),
                        downpaymentDTO.getContractTypeId());

        // Check if the payment date is valid
        DataValidation.isValidDate(downpaymentDTO.getTransactionDate().toString(), "Payment Date");

        Downpayment downpayment2 = downPaymentRepository
                .findByCustomerNumber(downpaymentDTO.getCustomerNumber());
        if (downpayment2 != null) {
            return ResponseEntity.status(400).body("Downpayment already paid");
        }
        if (leaseContract != null) {
            // Check if the downpayment is already paid
            // if (leaseContract.getDownpaymentReceived() != null) {
            // return ResponseEntity.status(400).body("Downpayment already paid");
            // }

            // Check if the payment amount is valid
            DataValidation.isNotNegativeAndZero(downpaymentDTO.getPaymentAmount().toString(), "Payment Amount");

            // Check if the payment amount is equal to the downpayment
            if (checkAmountsAreEqual(downpaymentDTO.getPaymentAmount().toString(),
                    String.valueOf(leaseContract.getContractType().getDownpayment())) == false)
                return ResponseEntity.status(400).body("Payment amount does not match the downpayment");

            // Update the downpayment status
            leaseContract.setDownpaymentReceived(BigDecimal.valueOf(downpaymentDTO.getPaymentAmount()));
            leaseContract.setDownpaymentTransDate(downpaymentDTO.getTransactionDate());

            Downpayment obj = new Downpayment();
            obj.setContractTypeId(leaseContract.getContractType().getContractTypeId());
            obj.setCustomerCode(leaseContract.getCustomerId());
            obj.setCustomerNumber(leaseContract.getCustomerNumber());
            obj.setDeployment(leaseContract.getContractType().getDeployment());
            obj.setVehicleType(leaseContract.getContractType().getTypeOfVehicle());
            obj.setPaymentAmount(BigDecimal.valueOf(downpaymentDTO.getPaymentAmount()));
            obj.setTransactionDate(Date.valueOf(downpaymentDTO.getTransactionDate()));

            // LeaseContract contract = leaseContractRepository.save(leaseContract);
            Downpayment downpayObj = downPaymentRepository.save(obj);
            if (downpayObj != null) {
                return ResponseEntity.ok("Payment status updated successfully");
            }

        } else {
            if (!contractTypeExists(downpaymentDTO.getContractTypeId())) {
                throw new DataNotFoundException("Contract Type not found");
            }
            Downpayment downpayment = new Downpayment();
            if (customerExists(downpaymentDTO.getCustomerNumber())) {

                Optional<Customer> customer = Optional
                        .ofNullable(customerRepository.findByCustomerNumber(downpaymentDTO.getCustomerNumber()));

                if (customer.isPresent()) {
                    downpayment.setCustomerNumber(customer.get().getCustomerNumber());
                    downpayment.setCustomerCode(customer.get().getCustomerNumber());
                    downpayment.setContractTypeId(customer.get().getCustomerContractDraft().getContractTypeId());
                    downpayment.setPaymentAmount(BigDecimal.valueOf(downpaymentDTO.getPaymentAmount()));
                    downpayment.setTransactionDate(Date.valueOf(downpaymentDTO.getTransactionDate()));
                    // downpayment.setVehicleType(VehicleType.valueOf(downpaymentDTO.getVehicleType()));
                    // downpayment.setDeployment(Deployment.valueOf(downpaymentDTO.getDeployment()));
                    downPaymentRepository.save(downpayment);
                    // System.out.println("Downpayment request is accepted");
                    return ResponseEntity.ok("Payment status updated successfully");
                }

            }

            downpayment.setContractTypeId(downpaymentDTO.getContractTypeId());
            downpayment.setCustomerNumber(downpaymentDTO.getCustomerNumber());
            downpayment.setCustomerCode(downpaymentDTO.getCustomerNumber());
            downpayment.setPaymentAmount(BigDecimal.valueOf(downpaymentDTO.getPaymentAmount()));
            downpayment.setTransactionDate(Date.valueOf(downpaymentDTO.getTransactionDate()));
            // downpayment.setVehicleType(VehicleType.valueOf(downpaymentDTO.getVehicleType()));
            // downpayment.setDeployment(Deployment.valueOf(downpaymentDTO.getDeployment()));
            Downpayment downpaymentObj = downPaymentRepository.save(downpayment);
            if (downpaymentObj != null) {
                throw new AcceptedException("Customer is not found.But Downpayment request is accepted");
            }

        }
        return ResponseEntity.status(400).body("Downpayment request is failed");

    }
}
