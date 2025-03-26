package com.spinteg.lv.controller;

import com.spinteg.lv.dto.DefaultedLeaseContractDTO;
import com.spinteg.lv.dto.TerminatedLeaseContractDTO;
import com.spinteg.lv.dto.mapper.DefaultedLeaseContractMapper;
import com.spinteg.lv.dto.mapper.TerminatedLeaseContractMapper;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contract/defaulted")
@CrossOrigin
@AllArgsConstructor
public class DefaultedLeaseContractController {

    private final LeaseContractRepository leaseContractRepository;

    @GetMapping("/all-leases")
    public List<DefaultedLeaseContractDTO> getAllLeaseContracts() {
        return leaseContractRepository.findByStatus(LeaseContractStatus.DEFAULTED).stream().map(DefaultedLeaseContractMapper::toDefaultedLeaseContractDTO).toList();
    }
}
