package com.spinteg.lv.controller;

import com.spinteg.lv.dto.AmortizationEntryUpdateDTO;
import com.spinteg.lv.dto.LeaseContractWithAmortizationDTO;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.service.AmortizationEntryUpdateService;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/schedule")
@AllArgsConstructor
@CrossOrigin
public class LeaseDailyScheduleController {
    
    private final LeaseContractRepository leaseContractRepository;
    private final AmortizationEntryUpdateService amortizationEntryUpdateService;

    @GetMapping("/contractId/{contractId}")
    public LeaseContractWithAmortizationDTO getAmortizationScheduleForContract(@PathVariable String contractId) {
        Optional<LeaseContract> leaseContract = leaseContractRepository.findById(UUID.fromString(contractId));
        return leaseContract.map(LeaseContractMapper::toLeaseContractWithAmortizationDTO).orElse(null);
    }

    @PostMapping("update-status/")
    public ResponseEntity getAmortizationScheduleForContract(
            @RequestBody AmortizationEntryUpdateDTO amortizationEntryUpdateDTO) {
        if (amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntryUpdateDTO)) {
            amortizationEntryUpdateService.updateOtherAmortizationEntries(amortizationEntryUpdateDTO);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PostMapping("update-status/bulk/")
    public ResponseEntity getAmortizationScheduleForContract(
            @RequestBody List<AmortizationEntryUpdateDTO> amortizationEntryUpdateDTOList) {
        for (AmortizationEntryUpdateDTO amortizationEntry : amortizationEntryUpdateDTOList) {
            if (!amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntry)) {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
        return ResponseEntity.ok().build();
    }
}
