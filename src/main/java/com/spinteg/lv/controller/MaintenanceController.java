package com.spinteg.lv.controller;

import com.spinteg.lv.repository.LeaseContractRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/maintenance")
@AllArgsConstructor
@CrossOrigin
public class MaintenanceController {

    private LeaseContractRepository leaseContractRepository;

    // Maintenance jobs -> SUPER_ADMIN only (sensitive / destructive)
    @PostMapping("update-past/pending-status/")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity updatePastPendingStatusesToDefault() {
        leaseContractRepository.updateLeaseContractToDefaulted();
        leaseContractRepository.updatePendingPaymentsToDefaultedForPast();
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-past/no-rent-day-future-status/")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity updatePastNoRentDatFutureStatusesToNoRentDayFuture() {
        leaseContractRepository.updateNoRentDayFutureAmortizationEntriesForPast();
        return ResponseEntity.ok().build();
    }
}
