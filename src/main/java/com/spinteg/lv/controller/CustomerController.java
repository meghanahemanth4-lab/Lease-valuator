package com.spinteg.lv.controller;

import com.spinteg.lv.dto.CustomerDTO;
import com.spinteg.lv.dto.CustomerInputDTO;
import com.spinteg.lv.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@AllArgsConstructor
@CrossOrigin
public class CustomerController {
    private final CustomerService customerService;

    // Create -> LEASE_CREATOR + SUPER_ADMIN
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerInputDTO dto) {
        return customerService.createNewCustomer(dto);
    }

    // View -> open to DEPLOYER + creators/admins
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    // Delete -> SUPER_ADMIN only (dangerous operation)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable String id) {
        return customerService.deleteCustomerById(id);
    }

    @GetMapping("/{customerId}/leases")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<Map<String, Object>> getLeasesByCustomer(@PathVariable String customerId) {
        return customerService.getLeasesByCustomer(customerId);
    }

    @GetMapping("/customer-draft")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> getCustomerDraft(@RequestParam String customerNumber) {
        return customerService.getCustomerDraft(customerNumber);
    }
}
