package com.spinteg.lv.service;

import com.spinteg.lv.ExceptionHandling.CustomDataMissingException;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.repository.LeaseContractRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeaseScheduleFactory {
    public static LeaseScheduleCalculator getLeaseScheduleGenerator(Frequency frequency, Double interestRatePerAnnum,
            Double downpayment, Double fixedDailyRental, int tenureInDays, LocalDate startDate,
            LeaseContractRepository leaseContractRepository) {
        switch (frequency) {
            case DAILY:
                return new DailyLeaseScheduleCalculator(interestRatePerAnnum, downpayment, fixedDailyRental,
                        tenureInDays, startDate);
            case MONTHLY:
                return new MonthlyLeaseScheduleCalculator(interestRatePerAnnum, downpayment, fixedDailyRental,
                        tenureInDays, startDate, leaseContractRepository);
            default:
                throw new CustomDataMissingException("Unsupported frequency type");
        }
    }
}
