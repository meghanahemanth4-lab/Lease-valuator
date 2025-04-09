package com.spinteg.lv.dto;

import java.time.LocalDate;

import lombok.Data;

@Data   
public class DownpaymentDTO {
    private String customerNumber;
    private String customerCode;    
    private String contractTypeId;
    private Double paymentAmount;       
    private LocalDate transactionDate;
    private String vehicleType; 
    private String deployment;

}
