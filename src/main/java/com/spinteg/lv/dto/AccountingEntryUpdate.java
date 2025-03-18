package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.Deployment;

import lombok.Data;

@Data
public class AccountingEntryUpdate {
    private int month;
    private int year;
    private Deployment deployment;
    private String vehicleType;
    
}
