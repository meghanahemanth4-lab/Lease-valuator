package com.spinteg.lv.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spinteg.lv.dto.NoRentDayEntryDTO;
import com.spinteg.lv.service.AmortizationEntryUpdateService;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@AllArgsConstructor
public class NoRentDayEntryController {

    private final AmortizationEntryUpdateService amortizationEntryUpdateService;

    @PostMapping("set-no-rent-day")
    public ResponseEntity<?> changeToNoRentDay(@RequestBody NoRentDayEntryDTO dayEntryDTO) {
        return amortizationEntryUpdateService.updateAmortizationEntryToNoRentDay(dayEntryDTO.getDate(), dayEntryDTO.getCountry());
    }
}
