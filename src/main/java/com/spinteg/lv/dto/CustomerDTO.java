package com.spinteg.lv.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.spinteg.lv.entity.CustomerContractDraft;

import jakarta.validation.constraints.Null;

@Data
@Builder
public class CustomerDTO {

    private String customerNumber;

    private String customerName;

    private String country;
    private String city;
    private String phoneNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contractTypeId;
    private boolean isLeaseContract;
    // Getters and Setters
    // Constructors
}
