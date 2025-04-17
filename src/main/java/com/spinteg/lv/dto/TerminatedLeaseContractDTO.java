package com.spinteg.lv.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TerminatedLeaseContractDTO {
    LeaseContractDTO leaseContractDTO;
    Integer activePeriods;
    Integer remainingPeriods;
    Integer paidPeriods;
    Integer defaultPeriods;
    BigDecimal totalMoneyReceived;
    BigDecimal principalReceived;
    BigDecimal interestReversal;
    BigDecimal interestRecognised;
    BigDecimal revenueRecognised;
    BigDecimal grossDebtor;

    // New Fields
    LocalDate startDate;
    LocalDate dateOfTermination;
    Integer numberOfNoRentDay;
    Integer noOfTimesDeployed;

}
