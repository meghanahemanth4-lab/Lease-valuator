package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.Deployment;

import lombok.Data;

@Data
public class AccountingEntryUpdate {
    private Integer month;
    private Integer year;
    private Deployment deployment;
    private String vehicleType;
    
}
