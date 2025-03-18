package com.spinteg.lv.data;

import lombok.Data;

import java.util.List;

@Data
public class MonthlyCashFlow {
    int tenure;
    int baseRent;
    int midMonthDays;
    int firstMonthDays;
    int lastMonthDays;
    int numberOfMidMonths;
    double interestRate;
    double firstMonthInstallment;
    double midMonthsInstallment;
    double lastMonthInstallment;
    double sumOfPvOfCf;
    double totalLeasePayments;
    double totalInterests;
    List<MonthlyValues> monthlyValuesList;
}
