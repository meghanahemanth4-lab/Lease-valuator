package com.spinteg.lv.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataNotFoundException;
import com.spinteg.lv.exception.ResponseHandler;
import com.spinteg.lv.dto.CustomerDTO;
import com.spinteg.lv.dto.CustomerDraftReponseDTO;
import com.spinteg.lv.dto.CustomerInputDTO;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.entity.Customer;
import com.spinteg.lv.entity.CustomerContractDraft;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.CustomerRepository;
import com.spinteg.lv.repository.LeaseContractRepository;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final LeaseContractRepository leaseContractRepository;
    private final CustomerRepository customerRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final HolidayService holidayservice;

    public ResponseEntity<?> createNewCustomer(@RequestBody CustomerInputDTO dto) {

        Customer customer = customerRepository.findByCustomerNumber(dto.getCustomerNumber());

        // Check if the customer number already exists
        if (customer != null) {
            throw new CustomDataMissingException(
                    "Customer Number already exists. Please enter a different Customer Number");
        }
        // Check if the contract type exists
        contractTypeRepository.findById(dto.getContractTypeId())
                .orElseThrow(
                        () -> new DataNotFoundException("Contract Type not found with ID: " + dto.getContractTypeId()));

        // Check if the start date and end date are valid
        holidayservice.getHolidaysDatesForCountryAndYear(dto.getCountry());

        Customer draft = new Customer();
        draft.setCustomerNumber(dto.getCustomerNumber());
        draft.setCustomerName(dto.getCustomerName());
        draft.setCustomerContractDraft(CustomerContractDraft.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .contractTypeId(dto.getContractTypeId())
                .build());

        draft.setCity(dto.getCity());
        draft.setCountry(dto.getCountry());
        draft.setPhoneNumber(dto.getPhoneNumber());
        draft.setLeaseContract(false); // default value
        draft = repository.save(draft);
        return ResponseHandler.handleResponseEntity("new Customer data created Successfully", draft, null);
    }

    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        List<CustomerDTO> dtos = repository.findAll().stream().map(customer -> {
            CustomerContractDraft draft = customer.getCustomerContractDraft();
            CustomerDTO dto = CustomerDTO.builder()
                    .customerNumber(customer.getCustomerNumber())
                    .customerName(customer.getCustomerName())
                    .country(customer.getCountry())
                    .city(customer.getCity())
                    .phoneNumber(customer.getPhoneNumber())
                    .contractTypeId(draft.getContractTypeId())
                    .startDate(draft.getStartDate())
                    .endDate(draft.getEndDate())
                    .isLeaseContract(customer.isLeaseContract())
                    .build();

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
        return repository.findById(id)
                .map(customer -> {
                    CustomerContractDraft draft = customer.getCustomerContractDraft();
                    CustomerDTO dto = CustomerDTO.builder()
                            .customerNumber(customer.getCustomerNumber())
                            .customerName(customer.getCustomerName())
                            .country(customer.getCountry())
                            .city(customer.getCity())
                            .contractTypeId(draft.getContractTypeId())
                            .startDate(draft.getStartDate())
                            .endDate(draft.getEndDate())
                            .phoneNumber(customer.getPhoneNumber())
                            .isLeaseContract(customer.isLeaseContract())
                            .build();
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> new DataNotFoundException("Customer not found"));
    }

    public ResponseEntity<Void> deleteCustomerById(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Map<String, Object>> getLeasesByCustomer(@PathVariable String customerId) {

        List<LeaseContract> leases = leaseContractRepository.findByCustomerNumber(customerId);

        if (leases.isEmpty()) {
            throw new DataNotFoundException("No leases found for customer with ID: " + customerId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalContracts", leases.size());
        response.put("leases", leases.stream().map(LeaseContractMapper::toLeaseContractDTO).toList());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getCustomerDraft(String customerNumber) {

        Customer customer = repository.findByCustomerNumber(customerNumber);

        if (customer != null) {
            // Check if the customer number already exists
            if (customer.isLeaseContract() == true) {
                throw new DataNotFoundException(
                        "Customer is already a lease contract. Please enter a different Customer Number");
            }

            CustomerContractDraft draft = customer.getCustomerContractDraft();
            ContractType contractType = contractTypeRepository.findById(draft.getContractTypeId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "Contract Type not found with ID: " + draft.getContractTypeId()));

            CustomerDraftReponseDTO response = new CustomerDraftReponseDTO();
            response.setContractTypeId(draft.getContractTypeId());
            response.setStartDate(draft.getStartDate().toString());
            response.setCustomerName(customer.getCustomerName());
            response.setCustomerNumber(customer.getCustomerNumber());
            response.setVehicleStatus(contractType.getVehicleStatus().toString());
            response.setCountry(customer.getCountry());
            response.setCity(customer.getCity());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(404).body("Customer not found with ID: " + customerNumber);
    }
}
