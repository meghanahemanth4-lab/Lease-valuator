package com.spinteg.lv.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TerminateLeaseDTO {
    private String customerNumber;
    private String contractTypeId;
    private LocalDate date;
}
