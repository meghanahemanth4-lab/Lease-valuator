package com.spinteg.lv.controller;

import com.spinteg.lv.dto.ActiveLeaseDTO;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.dto.mapper.ActiveLeaseMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/active-leases")
@CrossOrigin
@AllArgsConstructor
public class ActiveLeasesController {

    private final LeaseContractRepository leaseContractRepository;

    @GetMapping("/all")
    public List<ActiveLeaseDTO> getActiveLeases() {
        return leaseContractRepository.findByStatus(LeaseContractStatus.ACTIVE).stream()
                .map(ActiveLeaseMapper::toActiveLeaseDTO)
                .toList();
    }
    
     @GetMapping("/{id}")
    public ActiveLeaseDTO getActiveLeases(@PathVariable UUID id) {
        return leaseContractRepository.findById(id)
                .map(ActiveLeaseMapper::toActiveLeaseDTO)
                .orElse(null);
    }
}
