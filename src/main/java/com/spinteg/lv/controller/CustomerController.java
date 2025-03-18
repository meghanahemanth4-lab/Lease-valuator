package com.spinteg.lv.controller;

import com.spinteg.lv.dto.CustomerDTO;
import com.spinteg.lv.dto.CustomerInputDTO;
import com.spinteg.lv.service.CustomerService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@AllArgsConstructor
@CrossOrigin
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> createCustomer(@RequestBody CustomerInputDTO dto) {
        return customerService.createNewCustomer(dto);
    }

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable String id) {
        return customerService.deleteCustomerById(id);
    }

    @GetMapping("/{customerId}/leases")
    public ResponseEntity<Map<String, Object>> getLeasesByCustomer(@PathVariable String customerId) {
        return customerService.getLeasesByCustomer(customerId);
    }
}