package com.spinteg.lv.controller;

import com.spinteg.lv.dto.AmortizationEntryUpdateDTO;
import com.spinteg.lv.dto.LeaseContractWithAmortizationDTO;
import com.spinteg.lv.dto.TerminateLeaseDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.service.AmortizationEntryUpdateService;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public LeaseContractWithAmortizationDTO getAmortizationScheduleForContract(@PathVariable String contractId) {
        Optional<LeaseContract> leaseContract = leaseContractRepository.findById(UUID.fromString(contractId));
        return leaseContract.map(LeaseContractMapper::toLeaseContractWithAmortizationDTO).orElse(null);
    }

    // Update single amortization entry -> LEASE_CREATOR + SUPER_ADMIN + DEPUTY_ADMIN
    @PostMapping("update-status/")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> updateAmortizationStatus(
            @RequestBody AmortizationEntryUpdateDTO amortizationEntryUpdateDTO) {
        if (amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntryUpdateDTO.getAmortizationEntryId(),
                amortizationEntryUpdateDTO.getStatus(), amortizationEntryUpdateDTO.getTransactionDate())) {
            amortizationEntryUpdateService.updateOtherAmortizationEntries(amortizationEntryUpdateDTO);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    // Terminate -> LEASE_CREATOR + SUPER_ADMIN + DEPUTY_ADMIN
    @PostMapping("update-status/terminate")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody TerminateLeaseDTO terminateLeaseDTO) {
        LeaseContract leaseContract = leaseContractRepository.findByCustomerIdAndContractTypeId(
                terminateLeaseDTO.getCustomerNumber(), terminateLeaseDTO.getContractTypeId());
        AmortizationEntry amortizationEntry = leaseContract.getAmortizationEntries().stream()
                .filter(entry -> terminateLeaseDTO.getDate().equals(entry.getDate())).findFirst().get();
        amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntry.getAmortizationEntryId(),
                LeasePaymentStatus.TERMINATED, amortizationEntry.getTransactionDate());
        return ResponseEntity.ok().build();
    }

    // Bulk update -> LEASE_CREATOR + SUPER_ADMIN + DEPUTY_ADMIN
    @PostMapping("update-status/bulk/")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> updateAmortizationStatusBulk(
            @RequestBody List<AmortizationEntryUpdateDTO> amortizationEntryUpdateDTOList) {
        for (AmortizationEntryUpdateDTO amortizationEntry : amortizationEntryUpdateDTOList) {
            if (!amortizationEntryUpdateService.updateAmortizationEntry(amortizationEntry.getAmortizationEntryId(),
                    amortizationEntry.getStatus(), amortizationEntry.getTransactionDate())) {
                return ResponseEntity.unprocessableEntity().build();
            }
        }
        return ResponseEntity.ok().build();
    }
}
