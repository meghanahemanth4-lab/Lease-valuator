package com.spinteg.lv.service;

import com.spinteg.lv.data.MonthlyCashFlow;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.LeaseContractRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MonthlyLeaseScheduleCalculator implements LeaseScheduleCalculator {

        private static final Logger logger = LoggerFactory.getLogger(MonthlyLeaseScheduleCalculator.class);

        private final double interestRatePerAnnum;
        private final double fixedMonthlyPayment;
        private final double downpayment;
        private final int tenureInDays;
        private final LocalDate startDate;
        private final MonthlyCashFlowService monthlyCashFlowService;
        private final LeaseContractRepository leaseContractRepository;

        public MonthlyLeaseScheduleCalculator(Double interestRatePerAnnum, Double downpayment, Double fixedDailyRental,
                        int tenureInDays, LocalDate startDate, LeaseContractRepository leaseContractRepository) {
                this.interestRatePerAnnum = interestRatePerAnnum;
                this.downpayment = downpayment;
                this.fixedMonthlyPayment = fixedDailyRental; // Assuming fixedDailyRental should be assigned to
                                                             // fixedMonthlyPayment
                this.tenureInDays = tenureInDays;
                this.startDate = startDate;
                this.monthlyCashFlowService = new MonthlyCashFlowService(); // Assuming default initialization
                this.leaseContractRepository = leaseContractRepository; // Initialize the repository
        }

        @Override
        public List<AmortizationEntry> generateAmortizationSchedule(LeaseContract leaseContract, List<String> list) {
                MonthlyCashFlow monthlyCashFlow = monthlyCashFlowService.generateAmortizationSchedule(
                                leaseContract.getContractType().getTenure(),
                                leaseContract.getContractType().getBaseRent(),
                                (int) leaseContract.getContractType().getInterestRate());
                leaseContract.setSumOfPvOfCf(BigDecimal.valueOf(monthlyCashFlow.getSumOfPvOfCf())); // Convert double to
                                                                                                    // BigDecimal
                leaseContractRepository.save(leaseContract); // Save the lease contract
                return mapToAmortizationEntries(monthlyCashFlow, leaseContract, leaseContract.getStartDate());
        }

        public List<AmortizationEntry> mapToAmortizationEntries(MonthlyCashFlow cashFlow, LeaseContract leaseContract,
                        LocalDate startDate) {
                return cashFlow.getMonthlyValuesList().stream().map(monthlyValue -> {

                        LocalDate paymentDate = YearMonth.from(startDate.plusMonths(monthlyValue.getMonthNo() - 1))
                                        .atEndOfMonth();

                        return AmortizationEntry.builder()
                                        .amortizationEntryId(UUID.randomUUID())
                                        .leaseContract(leaseContract)
                                        .date(paymentDate) // Set last day of the month as payment date
                                        .period((long) monthlyValue.getMonthNo())
                                        .installment(monthlyValue.getMonthlyInstallment())
                                        .baseRent(monthlyValue.getMonthlyInstallment() == 0 ? 0.0
                                                        : leaseContract.getContractType().getBaseRent())
                                        .insurance(monthlyValue.getMonthlyInstallment() == 0 ? 0.0
                                                        : leaseContract.getContractType().getInsuranceAmount())
                                        .maintenance(monthlyValue.getMonthlyInstallment() == 0 ? 0.0
                                                        : leaseContract.getContractType().getMaintenanceAmount())
                                        .cumulativeInstallment(monthlyValue.getCumulativeInstallmentPaid())
                                        .closingBalance(monthlyValue.getClosingBalance())
                                        .outstandingBalance(monthlyValue.getOutstandingAmount())
                                        .interestComponent(monthlyValue.getInterestAmount())
                                        .principalComponent(monthlyValue.getPrincipalAmount())
                                        .cumulativePrincipal(
                                                        monthlyValue.getCumulativeInstallmentPaid()
                                                                        - monthlyValue.getCumulativeInterestPaid())
                                        .cumulativeInterest(monthlyValue.getCumulativeInterestPaid())
                                        .remainingAmount(monthlyValue.getOutstandingAmount())
                                        .pvOfCf(monthlyValue.getPvOfCf())
                                        .leasePaymentStatus(LeasePaymentStatus.PENDING)
                                        .build();
                }).collect(Collectors.toList());
        }
}
