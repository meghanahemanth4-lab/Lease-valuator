package com.spinteg.lv.controller;

import com.spinteg.lv.dto.CreateLeaseContractDTO;
import com.spinteg.lv.dto.LeaseContractDTO;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.repository.CustomerRepository;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import com.spinteg.lv.service.LeaseContractService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contract")
@CrossOrigin
@AllArgsConstructor
public class LeaseContractController {

    private final LeaseContractRepository leaseContractRepository;
    private final LeaseContractService leaseContractService;
    private final CustomerRepository customerRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public Optional<LeaseContractDTO> getLeaseContract(@PathVariable UUID id) {
        return leaseContractRepository.findById(id).map(LeaseContractMapper::toLeaseContractDTO);
    }

    @GetMapping("/all-leases")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public List<LeaseContractDTO> getAllLeaseContracts() {
        return leaseContractRepository.findAll().stream().map(LeaseContractMapper::toLeaseContractDTO).toList();
    }

    // Delete -> SUPER_ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public List<LeaseContract> deleteContract(@PathVariable UUID id) {
        leaseContractRepository.deleteById(id);
        return leaseContractRepository.findAll();
    }

    // Create -> LEASE_CREATOR and SUPER_ADMIN (and DEPUTY_ADMIN if you want)
    @PostMapping("/createLeaseContract")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPUTY_ADMIN')")
    public ResponseEntity<?> createLeaseContract(
            @RequestBody CreateLeaseContractDTO createLeaseContractDTO) {
        return leaseContractService.saveLeaseContract(createLeaseContractDTO);
    }
}
