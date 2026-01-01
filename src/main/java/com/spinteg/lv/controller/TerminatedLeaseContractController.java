package com.spinteg.lv.controller;

import com.spinteg.lv.dto.TerminatedLeaseContractDTO;
import com.spinteg.lv.dto.mapper.TerminatedLeaseContractMapper;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contract/terminated")
@CrossOrigin
@AllArgsConstructor
public class TerminatedLeaseContractController {

    private final LeaseContractRepository leaseContractRepository;

    @GetMapping("/all-leases")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public List<TerminatedLeaseContractDTO> getAllLeaseContracts() {
        return leaseContractRepository
                .findByStatusIn(List.of(LeaseContractStatus.TERMINATED, LeaseContractStatus.SHORT_CLOSE))
                .stream().map(TerminatedLeaseContractMapper::toTerminatedLeaseContractDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public List<TerminatedLeaseContractDTO> getTerminatedContractById(@PathVariable UUID id) {
        return leaseContractRepository.findById(id).stream()
                .filter(leaseContract -> leaseContract.getStatus().equals(LeaseContractStatus.TERMINATED))
                .map(TerminatedLeaseContractMapper::toTerminatedLeaseContractDTO).toList();
    }

    @GetMapping("/all/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public List<TerminatedLeaseContractDTO> getContractById(@PathVariable UUID id) {
        return leaseContractRepository.findById(id).stream()
                .map(TerminatedLeaseContractMapper::toTerminatedLeaseContractDTO).toList();
    }
}
