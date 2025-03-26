package com.spinteg.lv.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PaymentApiDTO {
    private String customerNumber;
    private String contractTypeId;
    private LocalDate date;
    private Double paymentAmount;
    private LocalDate trasactionDate;
}
