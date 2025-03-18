package com.spinteg.lv.controller;

import com.opencsv.CSVReader;
import com.spinteg.lv.dto.CreateContractTypeDTO;
import com.spinteg.lv.dto.UpdateContractTypeDTO;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.service.ContractTypeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contract-types")
@AllArgsConstructor
@CrossOrigin
public class ContractTypeController {

    private final ContractTypeService contractTypeService;

    @GetMapping("/allContractTypes")
    public ResponseEntity<List<ContractType>> getAllContractTypes() {
        return ResponseEntity.ok(contractTypeService.getALlContractTypes());
    }

    @GetMapping("/{contractTypeId}")
    public ResponseEntity<ContractType> getContractTypeById(@PathVariable("contractTypeId") String contractTypeId) {
        ContractType contractType = contractTypeService.getContractTypeById(contractTypeId);
        
        return ResponseEntity.ok(contractType);
    }

    @GetMapping("/contract-type-ids")
    public ResponseEntity<List<String>> getAllContractTypeIds() {
        List<String> contractTypeIds = contractTypeService.getAllContractTypeIds();
        return ResponseEntity.ok(contractTypeIds);
    }

    @PostMapping("/createContractType")
    public ResponseEntity<ContractType> createLeaseContract(@RequestBody CreateContractTypeDTO createContractTypeDTO) {
        return ResponseEntity.ok(contractTypeService.saveContractType(createContractTypeDTO));
    }

    @GetMapping("/nullBaseRent")
    public ResponseEntity<List<ContractType>> getContractTypesWithNullBaseRent() {
        return ResponseEntity.ok(contractTypeService.getContractTypesWithNullBaseRent());
    }

    @PostMapping("/updateContractTypeFields/{contractTypeId}")
    public ResponseEntity<?> updateContractTypeFields(@RequestBody UpdateContractTypeDTO contractType,
            @PathVariable("contractTypeId") String contractTypeId) {
        return ResponseEntity.ok(contractTypeService.updateContractTypeFields(contractTypeId, contractType));
    }
}
