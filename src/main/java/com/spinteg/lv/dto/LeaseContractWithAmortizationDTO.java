package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.VehicleStatus;
import com.spinteg.lv.entity.ContractType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class LeaseContractWithAmortizationDTO {
    private UUID contractNo;
    private String customerId;
    private String vehicleNo;
    private String customerName;
    private String customerNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaseContractStatus status;
    private BigDecimal revenuePending;
    private BigDecimal valueOfLeaseToday;
    private ContractType contractType;
    private VehicleStatus vehicleStatus;
    private String city;
    private String country;
    private BigDecimal sumOfPvOfCf;
    private BigDecimal totalLeasePayments;
    private BigDecimal totalInterest;
    private List<AmortizationEntryDto> leasePaymentEntries; // This will be populated separately

    // Getters and Setters
}
