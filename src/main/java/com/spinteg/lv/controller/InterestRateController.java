package com.spinteg.lv.controller;

import com.spinteg.lv.ExceptionHandling.ResponseHandler;
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
    public ResponseEntity<?> setInterestRate(@RequestBody String rate) {
        InterestRate updatedRate = interestRateService.setInterestRate(rate);
        contractTypeService.updateInterestRateForAllContracts(Float.parseFloat(rate));
        return ResponseHandler.handleResponseEntity("Interest Rate is updated Successfully ", updatedRate);
    }

    @GetMapping
    public ResponseEntity<InterestRate> getInterestRate() {
        return ResponseEntity.ok(interestRateService.getInterestRate());
    }
}