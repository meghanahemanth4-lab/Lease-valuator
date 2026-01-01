package com.spinteg.lv.controller;

import com.spinteg.lv.dto.DefaultedLeaseContractDTO;
import com.spinteg.lv.dto.mapper.DefaultedLeaseContractMapper;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contract/defaulted")
@CrossOrigin
@AllArgsConstructor
public class DefaultedLeaseContractController {

    private final LeaseContractRepository leaseContractRepository;

    @GetMapping("/all-leases")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public List<DefaultedLeaseContractDTO> getAllLeaseContracts() {
        return leaseContractRepository.findByStatus(LeaseContractStatus.DEFAULTED).stream()
                .map(DefaultedLeaseContractMapper::toDefaultedLeaseContractDTO)
                .toList();
    }
}
