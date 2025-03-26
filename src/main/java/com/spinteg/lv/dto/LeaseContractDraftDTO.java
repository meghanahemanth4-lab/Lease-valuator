package com.spinteg.lv.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LeaseContractDraftDTO {
    
    private String customerId;
    private String customerName;

    private LocalDate startDate;
    private LocalDate endDate;
    private String contractTypeId;

    private String country;
    private String city;
    private String phoneNumber;
}
