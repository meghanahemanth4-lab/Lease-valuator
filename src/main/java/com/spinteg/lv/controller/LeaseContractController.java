package com.spinteg.lv.controller;

import com.spinteg.lv.dto.CreateLeaseContractDTO;
import com.spinteg.lv.dto.LeaseContractDTO;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.repository.CustomerRepository;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.dto.mapper.LeaseContractMapper;
import com.spinteg.lv.service.LeaseContractService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;ug
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/contract")
@CrossOrigin
@AllArgsConstructor
public class LeaseContractController {

    private final LeaseContractRepository leaseContractRepository;
    private final LeaseContractService leaseContractService;
    private final CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public Optional<LeaseContractDTO> getLeaseContract(@PathVariable UUID id) {
        return leaseContractRepository.findById(id).map(LeaseContractMapper::toLeaseContractDTO);
    }

    @GetMapping("/all-leases")
    public List<LeaseContractDTO> getAllLeaseContracts() {
        return leaseContractRepository.findAll().stream().map(LeaseContractMapper::toLeaseContractDTO).toList();
    }

    @DeleteMapping("/{id}")
    public List<LeaseContract> deleteContract(@PathVariable UUID id) {
        leaseContractRepository.deleteById(id);
        return leaseContractRepository.findAll();
    }

    @PostMapping("/createLeaseContract")
    public ResponseEntity<?> createLeaseContract(
            @RequestBody CreateLeaseContractDTO createLeaseContractDTO) {
        return leaseContractService.saveLeaseContract(createLeaseContractDTO);
    }
}
