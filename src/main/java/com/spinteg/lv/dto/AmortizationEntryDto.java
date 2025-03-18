package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AmortizationEntryDto {

    private UUID leasePaymentEntryId;
    private LocalDate date;
    private Long period;
    private Double installment;
    private Double interestComponent;
    private Double principalComponent;
    private Double remainingAmount;
    private LeasePaymentStatus leasePaymentStatus;
    private Double pvOfCf;
    private Double cumulativeInterest;
    private Double cumulativePrincipal;
    private Double cumulativeInstallment;
    private Double closingBalance;
    private Double percentageOfInterest;
    private Double percentageOfPrincipal;

    // Getters and Setters
}
