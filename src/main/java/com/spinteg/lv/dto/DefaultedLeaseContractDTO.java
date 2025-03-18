package com.spinteg.lv.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class DefaultedLeaseContractDTO {
    LeaseContractDTO leaseContractDTO;
    Integer activePeriods;
    Integer remainingPeriods;
    Integer paidPeriods;
    Integer defaultPeriods;
    BigDecimal totalPaymentReceived;
    BigDecimal totalPrincipalReceived;
    BigDecimal totalInterestReceived;
    BigDecimal totalDefault;
    BigDecimal interestDefault;
    BigDecimal principalDefault;
    BigDecimal expectedTotalPayment;

    // New Field
    LocalDate startDate;
    LocalDate lastDefaultedDate;
    Integer numberOfNoRentDay;
    Integer noOfTimesRedeployed;
}
