package com.spinteg.lv.service;

import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.LeaseContractRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MonthlyV2LeaseScheduleCalculator implements LeaseScheduleCalculator {

    private final double interestRatePerAnnum;
    private final double fixedMonthlyPayment;
    private final double downpayment;
    private final int tenureInDays;
    private final LocalDate startDate;
    private final LeaseContractRepository leaseContractRepository;

    public MonthlyV2LeaseScheduleCalculator(Double interestRatePerAnnum, Double downpayment, Double fixedMonthlyPayment,
            int tenureInDays, LocalDate startDate, LeaseContractRepository leaseContractRepository) {
        this.interestRatePerAnnum = interestRatePerAnnum;
        this.downpayment = downpayment;
        this.fixedMonthlyPayment = fixedMonthlyPayment;
        this.tenureInDays = tenureInDays;
        this.startDate = startDate;
        this.leaseContractRepository = leaseContractRepository;
    }

    @Override
    public List<AmortizationEntry> generateAmortizationSchedule(LeaseContract leaseContract, List<String> holidaysList) {
        List<AmortizationEntry> schedule = new ArrayList<>();
        int periods = tenureInDays; // Use tenure as number of periods
        double outstandingAmount = fixedMonthlyPayment * periods; // Simplified
        double monthlyInterestRate = interestRatePerAnnum / 1200;
        double cumulativeInstallment = 0, cumulativeInterest = 0, cumulativePrincipal = 0;

        // Set paymentDate to the next 1st of the month after startDate
        LocalDate paymentDate = startDate.getDayOfMonth() == 1
                ? startDate
                : startDate.plusMonths(1).withDayOfMonth(1);

        for (int i = 0; i < periods; i++) {
            LocalDate emiDate = paymentDate.plusMonths(i);

            double interestComponent = outstandingAmount * monthlyInterestRate;
            double principalComponent = fixedMonthlyPayment - interestComponent;
            cumulativeInstallment += fixedMonthlyPayment;
            cumulativeInterest += interestComponent;
            cumulativePrincipal += principalComponent;
            outstandingAmount -= principalComponent;

            AmortizationEntry entry = AmortizationEntry.builder()
                    .amortizationEntryId(UUID.randomUUID())
                    .leaseContract(leaseContract)
                    .date(emiDate)
                    .period((long) (i + 1))
                    .installment(fixedMonthlyPayment)
                    .baseRent(leaseContract.getContractType().getBaseRent() == null ? 0.0 : leaseContract.getContractType().getBaseRent().doubleValue())
                    .insurance(leaseContract.getContractType().getInsuranceAmount() == null ? 0.0 : leaseContract.getContractType().getInsuranceAmount().doubleValue())
                    .maintenance(leaseContract.getContractType().getMaintenanceAmount() == null ? 0.0 : leaseContract.getContractType().getMaintenanceAmount().doubleValue())
                    .swapAmount(leaseContract.getContractType().getSwapAmount() == null ? 0.0 : leaseContract.getContractType().getSwapAmount().doubleValue())
                    .cumulativeInstallment(cumulativeInstallment)
                    .closingBalance(outstandingAmount)
                    .outstandingBalance(outstandingAmount)
                    .interestComponent(interestComponent)
                    .principalComponent(principalComponent)
                    .cumulativePrincipal(cumulativePrincipal)
                    .cumulativeInterest(cumulativeInterest)
                    .remainingAmount(outstandingAmount)
                    .pvOfCf(fixedMonthlyPayment / Math.pow(1 + monthlyInterestRate, i + 1))
                    .leasePaymentStatus(LeasePaymentStatus.PENDING)
                    .build();
            schedule.add(entry);
        }
        leaseContract.setSumOfPvOfCf(BigDecimal.valueOf(schedule.stream().mapToDouble(AmortizationEntry::getPvOfCf).sum()));
        leaseContractRepository.save(leaseContract);
        return schedule;
    }
}