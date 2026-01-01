package com.spinteg.lv.controller;

import com.spinteg.lv.dto.NoRentDayEntryDTO;
import com.spinteg.lv.service.AmortizationEntryUpdateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@AllArgsConstructor
public class NoRentDayEntryController {

    private final AmortizationEntryUpdateService amortizationEntryUpdateService;

    // Changing to No-Rent-Day is a maintenance/financial change -> SUPER_ADMIN only
    @PostMapping("set-no-rent-day")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> changeToNoRentDay(@RequestBody NoRentDayEntryDTO dayEntryDTO) {
        return amortizationEntryUpdateService.updateAmortizationEntryToNoRentDay(dayEntryDTO.getDate(), dayEntryDTO.getCountry());
    }
}
