package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LeaseContractDTO {
    private UUID contractNo;
    private String customerNumber;
    private String vehicleNo;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaseContractStatus leaseContractStatus;
    private BigDecimal revenuePending;
    private BigDecimal valueOfLeaseToday;
    private String city;
    private String country;
    private ContractTypeDTO contractTypeDTO;
    private VehicleStatus vehicleStatus;
    private Integer noOfTimesDeployed;
    private BigDecimal sumOfPvOfCf;

}