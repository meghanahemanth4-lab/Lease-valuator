package com.spinteg.lv.dto;

import lombok.Data;

@Data
public class AccountingEntryDTO {
    private Float leaseReceivableDr;                    //1
    private Float bikeSaleFinanceLeaseCr;
    private Float rentalCollectionWalletDr;             //2
    private Float rentalIncomeCr;   
    private Float maintenanceCollectionWalletDr;        //3
    private Float maintenanceIncomeCr;
    private Float insuranceIncomeCollectionWalletDr;    //4
    private Float insuranceIncomeCr;
    private Float interestIncomeLeaseReceivableDr;      //5
    private Float interestIncomeCr;
    private Float leaseReceivableInterestIncomeDr;      //6
    private Float leaseReceivableInterestIncomeCr;
    private Float bikeSaleFinanceLeaseDr;               //7
    private Float leaseReceivableCr;
    private Float swapCollectionWalletDr;               //8
    private Float swapRevenueCr;
    private Float tvaCr;
    private Float downpaymentCollectionWalletDr;        //9
    private Float downpaymentIncomeCr;
    private Float serviceCollectionWalletDr;            //10
    private Float serviceIncomeCr;
    private Float bikeSaleFinanceLeaseDebtorDr;         //11
    private Float leaseReceivableFinanceDebtorCr;    
}
