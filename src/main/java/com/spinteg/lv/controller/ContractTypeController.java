package com.spinteg.lv.controller;

import com.spinteg.lv.dto.CreateContractTypeDTO;
import com.spinteg.lv.dto.UpdateContractTypeDTO;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.service.ContractTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contract-types")
@AllArgsConstructor
@CrossOrigin
public class ContractTypeController {

    private final ContractTypeService contractTypeService;

    // View endpoints -> allow DEPLOYER and others
    @GetMapping("/allContractTypes")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<List<ContractType>> getAllContractTypes() {
        return ResponseEntity.ok(contractTypeService.getALlContractTypes());
    }

    @GetMapping("/{contractTypeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<ContractType> getContractTypeById(@PathVariable("contractTypeId") String contractTypeId) {
        ContractType contractType = contractTypeService.getContractTypeById(contractTypeId);
        return ResponseEntity.ok(contractType);
    }

    @GetMapping("/contract-type-ids")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<List<String>> getAllContractTypeIds() {
        List<String> contractTypeIds = contractTypeService.getAllContractTypeIds();
        return ResponseEntity.ok(contractTypeIds);
    }

    // Create -> only CONTRACT_TYPE_CREATOR and SUPER_ADMIN
    @PostMapping("/createContractType")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_CONTRACT_TYPE_CREATOR')")
    public ResponseEntity<?> createLeaseContract(@RequestBody CreateContractTypeDTO createContractTypeDTO) {
        return contractTypeService.saveContractType(createContractTypeDTO);
    }

    @GetMapping("/nullBaseRent")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR','ROLE_LEASE_CREATOR','ROLE_DEPLOYER')")
    public ResponseEntity<List<ContractType>> getContractTypesWithNullBaseRent() {
        return ResponseEntity.ok(contractTypeService.getContractTypesWithNullBaseRent());
    }

    // Update -> CONTRACT_TYPE_CREATOR, DEPUTY_ADMIN, SUPER_ADMIN
    @PostMapping("/updateContractTypeFields/{contractTypeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_DEPUTY_ADMIN','ROLE_CONTRACT_TYPE_CREATOR')")
    public ResponseEntity<?> updateContractTypeFields(@RequestBody UpdateContractTypeDTO contractType,
                                                      @PathVariable("contractTypeId") String contractTypeId) {
        return contractTypeService.updateContractTypeFields(contractTypeId, contractType);
    }
}
