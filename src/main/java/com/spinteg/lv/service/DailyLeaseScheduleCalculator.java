package com.spinteg.lv.service;

import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class DailyLeaseScheduleCalculator implements LeaseScheduleCalculator {

    private static final Logger logger = LoggerFactory.getLogger(DailyLeaseScheduleCalculator.class);

    private final double interestRatePerAnnum;
    private final double fixedDailyPayment;
    private final double downpayment;
    private final int tenureInDays;
    private final LocalDate startDate;
    private DateCalculator dateCalculator;

    public DailyLeaseScheduleCalculator(double interestRatePerAnnum, double fixedDailyPayment, double downpayment,
            int tenureInDays, LocalDate startDate) {
        this.interestRatePerAnnum = interestRatePerAnnum;
        this.fixedDailyPayment = fixedDailyPayment;
        this.downpayment = downpayment;
        this.tenureInDays = tenureInDays;
        this.startDate = startDate;
    }

    public double calculateOutstandingAmount(double dailyInterestRate, int totalDays) {
        double outstandingAmount = 0.0;
        LocalDate currentDate = startDate;

        for (int days = 0; days <= totalDays; days++) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SUNDAY && !currentDate.equals(startDate)) {
                double discountFactor = Math.pow(1 + dailyInterestRate, days); // (1 + r)^days
                outstandingAmount += fixedDailyPayment / discountFactor; // Discounted payment
            }
            currentDate = currentDate.plusDays(1);
        }

        return outstandingAmount;
    }

    @Override
    public List<AmortizationEntry> generateAmortizationSchedule(LeaseContract leaseContract,
            List<String> holidaysList) {
        List<AmortizationEntry> schedule = new ArrayList<>();
        double dailyInterestRate = interestRatePerAnnum / (100 * 365); // Convert percentage to decimal
        dateCalculator = new DateCalculator(startDate, tenureInDays);

        double outstandingAmount = calculateOutstandingAmount(dailyInterestRate, dateCalculator.getTotalDays());
        LocalDate currentDate = startDate;
        var cumulativeInstallment = 0.0;
        var cumulativeInterest = 0.0;
        var cumulativePrincipal = 0.0;

        leaseContract.setSumOfPvOfCf(BigDecimal.valueOf(outstandingAmount));

        for (int paymentDay = 0, interestBearingDay = 0; paymentDay < tenureInDays; interestBearingDay++) {
            double discountFactor = Math.pow(1 + dailyInterestRate, interestBearingDay); // (1 + r)^days
            if (currentDate.getDayOfWeek() != DayOfWeek.SUNDAY && !currentDate.equals(startDate)
                    && !isHoliday(currentDate, holidaysList)) {
                double installment = fixedDailyPayment;
                outstandingAmount -= installment;
                double interestComponent = outstandingAmount * dailyInterestRate;
                double principalComponent = fixedDailyPayment - interestComponent;
                cumulativeInstallment += installment;
                cumulativePrincipal += principalComponent;
                cumulativeInterest += interestComponent;
                AmortizationEntry entry = AmortizationEntry.builder()
                        .leaseContract(leaseContract)
                        .date(currentDate)
                        .period(ChronoUnit.DAYS.between(startDate, currentDate))
                        .installment(Double.valueOf(leaseContract.getContractType().getInstallment()))
                        .baseRent(Double.valueOf(leaseContract.getContractType().getBaseRent()))
                        .insurance(Double.valueOf(leaseContract.getContractType().getInsuranceAmount()))
                        .maintenance(Double.valueOf(leaseContract.getContractType().getMaintenanceAmount()))
                        .swap(Double.valueOf(leaseContract.getContractType().getSwap())) // <-- Add this line
                        .interestComponent(interestComponent)
                        .cumulativePrincipal(cumulativePrincipal)
                        .cumulativeInstallment(cumulativeInstallment)
                        .cumulativeInterest(cumulativeInterest)
                        .closingBalance(outstandingAmount + interestComponent - installment)
                        .principalComponent(principalComponent)
                        .remainingAmount(outstandingAmount)
                        .pvOfCf(fixedDailyPayment / discountFactor)
                        .build();

                schedule.add(entry);
                outstandingAmount += interestComponent;
                currentDate = currentDate.plusDays(1);
                paymentDay++;
            } else {
                double interestComponent = outstandingAmount * dailyInterestRate;
                double principalComponent = 0.0;
                double installment = 0.0;
                cumulativeInstallment += installment;
                cumulativePrincipal += principalComponent;
                cumulativeInterest += interestComponent;
                AmortizationEntry entry = AmortizationEntry.builder()
                        .leaseContract(leaseContract)
                        .date(currentDate)
                        .period(ChronoUnit.DAYS.between(startDate, currentDate))
                        .installment(installment)
                        .baseRent(0.0)
                        .maintenance(0.0)
                        .swap(0.0) // <-- Add this line
                        .insurance(0.0)
                        .interestComponent(interestComponent)
                        .cumulativePrincipal(cumulativePrincipal)
                        .cumulativeInstallment(cumulativeInstallment)
                        .cumulativeInterest(cumulativeInterest)
                        .closingBalance(outstandingAmount + interestComponent - installment)
                        .principalComponent(principalComponent)
                        .remainingAmount(outstandingAmount)
                        .leasePaymentStatus(LeasePaymentStatus.NO_RENT_DAY_FUTURE)
                        .pvOfCf(0.0)
                        .build();

                schedule.add(entry);
                outstandingAmount += interestComponent;

                currentDate = currentDate.plusDays(1);
            }
        }
        leaseContract.setEndDate(currentDate.minusDays(1));
        // Convert double to BigDecimal
        return schedule;
    }

    public static boolean isHoliday(LocalDate date, List<String> list) {
        boolean flag = list.stream()
                .anyMatch(holidayDate -> DateMatcher.isMatchingDateOrNot(date, holidayDate));
        return flag;
    }

}