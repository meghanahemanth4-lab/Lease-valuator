package com.spinteg.lv.controller;

import com.spinteg.lv.exception.ResponseHandler;
import com.spinteg.lv.entity.InterestRate;
import com.spinteg.lv.service.InterestRateService;
import com.spinteg.lv.service.ContractTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interest-rate")
@AllArgsConstructor
@CrossOrigin
public class InterestRateController {

    private final InterestRateService interestRateService;
    private final ContractTypeService contractTypeService;

    // Update interest rate -> SUPER_ADMIN only
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    public ResponseEntity<?> setInterestRate(@RequestBody String rate) {
        InterestRate updatedRate = interestRateService.setInterestRate(rate);
        contractTypeService.updateInterestRateForAllContracts(Float.parseFloat(rate));
        return ResponseHandler.handleResponseEntity("Interest Rate is updated Successfully ", updatedRate, null);
    }

    // View -> allow DEPLOYER + others
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<InterestRate> getInterestRate() {
        return ResponseEntity.ok(interestRateService.getInterestRate());
    }
}
