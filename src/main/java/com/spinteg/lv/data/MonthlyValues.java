package com.spinteg.lv.data;

import lombok.Data;

@Data
public class MonthlyValues {
    int monthNo;
    double monthlyInstallment;
    double cumulativeInstallmentPaid;
    double outstandingAmount;
    double pvOfCf;
    double interestAmount;
    double principalAmount;
    double closingBalance;
    double cumulativeInterestPaid;
    double interestPercentage;
    double principalPercentage;
}
