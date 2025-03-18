package com.spinteg.lv.controller;

import com.spinteg.lv.entity.InterestRate;
import com.spinteg.lv.service.InterestRateService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.spinteg.lv.service.ContractTypeService;

@RestController
@RequestMapping("/interest-rate")
@AllArgsConstructor
@CrossOrigin
public class InterestRateController {

    private final InterestRateService interestRateService;
    private final ContractTypeService contractTypeService;

    @PostMapping
    public ResponseEntity<InterestRate> setInterestRate(@RequestBody float rate) {
        InterestRate updatedRate = interestRateService.setInterestRate(rate);
        contractTypeService.updateInterestRateForAllContracts(rate);
        return ResponseEntity.ok(updatedRate);
    }

    @GetMapping
    public ResponseEntity<InterestRate> getInterestRate() {
        return ResponseEntity.ok(interestRateService.getInterestRate());
    }
}