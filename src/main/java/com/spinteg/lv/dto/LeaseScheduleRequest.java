package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.Frequency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LeaseScheduleRequest {
    private Double interestRatePerAnnum;
    private Double fixedDailyPayment;
    private Double downpayment;
    private int tenureInDays;
    private LocalDate startDate;
    private Frequency frequency;
}
