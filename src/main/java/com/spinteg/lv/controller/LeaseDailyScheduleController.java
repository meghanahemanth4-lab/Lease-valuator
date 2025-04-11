package com.spinteg.lv.controller;

import com.spinteg.lv.dto.AmortizationEntryUpdateDTO;
import com.spinteg.lv.dto.LeaseContractWithAmortizationDTO;
import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.dto.TerminateLeaseDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
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
    public ResponseEntity updateAmortizationStatus(
            @RequestBody AmortizationEntryUpdateDTO amortizationEntryUpdateDTO) {
        if (amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntryUpdateDTO.getAmortizationEntryId(), amortizationEntryUpdateDTO.getStatus())) {
            amortizationEntryUpdateService.updateOtherAmortizationEntries(amortizationEntryUpdateDTO);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PostMapping("update-status/terminate")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody TerminateLeaseDTO terminateLeaseDTO) {
        LeaseContract leaseContract = leaseContractRepository.findByCustomerIdAndContractTypeId(terminateLeaseDTO.getCustomerNumber(), terminateLeaseDTO.getContractTypeId());
        AmortizationEntry amortizationEntry = leaseContract.getAmortizationEntries().stream().filter(entry -> terminateLeaseDTO.getDate().equals(entry.getDate())).findFirst().get();
        amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntry.getAmortizationEntryId(), LeasePaymentStatus.TERMINATED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-status/bulk/")
    public ResponseEntity updateAmortizationStatusBulk(
            @RequestBody List<AmortizationEntryUpdateDTO> amortizationEntryUpdateDTOList) {
        for (AmortizationEntryUpdateDTO amortizationEntry : amortizationEntryUpdateDTOList) {
            if (!amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntry.getAmortizationEntryId(), amortizationEntry.getStatus())) {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
        return ResponseEntity.ok().build();
    }
}
