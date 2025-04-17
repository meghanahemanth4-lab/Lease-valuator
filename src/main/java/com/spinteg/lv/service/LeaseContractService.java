package com.spinteg.lv.service;

import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.ValidationUtil;
import com.spinteg.lv.dto.CreateLeaseContractDTO;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.entity.Customer;
import com.spinteg.lv.entity.CustomerContractDraft;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.VehicleStatus;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.CustomerRepository;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LeaseContractService {

    private final LeaseContractRepository leaseContractRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final AmortizationEntryRepository amortizationEntryRepository;
    private final CustomerRepository customerRepository;
    private final HolidayService holidayservice;

    public ResponseEntity<?> saveLeaseContract(CreateLeaseContractDTO dto) {

        if (checkRequiredFields(dto) == false) {
            throw new CustomDataMissingException("Please fill all the required fields");
        }

        ContractType contractType = contractTypeRepository.findById(dto.getContractTypeId())
                .orElseThrow(() -> new CustomDataMissingException("Invalid contract type ID"));
        if (isNullBasesContract(contractType.getContractTypeId()) == true) {
            throw new CustomDataMissingException("Please enter the Base Rent in the Installment Amount Split");
        }

        try {
            VehicleStatus.valueOf(dto.getVehicleStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomDataMissingException("Invalid Vehicle Status: " + dto.getVehicleStatus());
        }

        LeaseContract leaseContract = new LeaseContract();

        leaseContract.setStartDate(dto.getStartDate());
        leaseContract.setEndDate(null);
        leaseContract.setCustomerId(dto.getCustomerNumber());
        leaseContract.setCountry(dto.getCountry());
        leaseContract.setCity(dto.getCity());
        leaseContract.setVehicleStatus(VehicleStatus.valueOf(dto.getVehicleStatus()));
        leaseContract.setNoOfTimesDeployed(dto.getNoOfTimesDeployed());
        leaseContract.setCustomerName(dto.getCustomerName());
        leaseContract.setCustomerNumber(dto.getCustomerNumber());
        leaseContract.setContractType(contractType);
        leaseContract.setVehicleNo(dto.getVehicleNumber());

        List<String> holidaysList = holidayservice.getHolidaysDatesForCountry(dto.getCountry());

        LeaseScheduleCalculator generator = LeaseScheduleFactory.getLeaseScheduleGenerator(
                contractType.getPaymentFrequency(), (double) contractType.getInterestRate(),
                Double.valueOf(contractType.getBaseRent()), Double.valueOf(contractType.getDownpayment()),
                contractType.getTenure(), dto.getStartDate(), leaseContractRepository); // Pass leaseContractRepository

        // Generate and return the schedule
        saveLeaseCustomerDetails(dto);
        leaseContractRepository.save(leaseContract);
        amortizationEntryRepository.saveAll(generator.generateAmortizationSchedule(leaseContract, holidaysList));
        return ResponseEntity.ok(leaseContract);
    }

    public Customer saveLeaseCustomerDetails(CreateLeaseContractDTO dto) {
        if (customerRepository.existsByCustomerNumber(dto.getCustomerNumber())) {

            if (customerRepository.findByCustomerNumber(dto.getCustomerNumber()).isLeaseContract() == true) {
                throw new CustomDataMissingException(
                        "Customer Number already exists with a different or same contract type");
            }
            Customer customer = customerRepository.findByCustomerNumber(dto.getCustomerNumber());
            customer.setCustomerContractDraft(
                    CustomerContractDraft.builder()
                            .startDate(dto.getStartDate())
                            .contractTypeId(dto.getContractTypeId()).build());
            customer.setLeaseContract(false);
            return customerRepository.saveAndFlush(customer);
        } else {
            Customer customer = Customer.builder()
                    .customerNumber(dto.getCustomerNumber())
                    .customerName(dto.getCustomerName())
                    .country(dto.getCountry())
                    .city(dto.getCity())
                    .customerContractDraft(
                            CustomerContractDraft.builder()
                                    .startDate(dto.getStartDate())
                                    .contractTypeId(dto.getContractTypeId()).build())
                    .isLeaseContract(true)
                    .build();
            return customerRepository.saveAndFlush(customer);
        }
    }

    public boolean isNullBasesContract(String contractTypeId) {
        return contractTypeRepository.findById(contractTypeId).get().getBaseRent() == null;
    }

    public boolean checkRequiredFields(CreateLeaseContractDTO dto) {
        boolean isVaild = ValidationUtil.checkRequiredFields(dto, "vehicleNumber");

        return isVaild;
    }

}
