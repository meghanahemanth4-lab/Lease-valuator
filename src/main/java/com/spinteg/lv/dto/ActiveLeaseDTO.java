package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.VehicleStatus;
import com.spinteg.lv.entity.enums.VehicleType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ActiveLeaseDTO {
    private UUID contractNo;
    private String contractType;
    private String country;
    private String city;
    private String customerId;
    private String customerName;
    private VehicleType vehicleType;
    private String vehicleNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Deployment deployment;
    private VehicleStatus vehicleStatus;
    private Integer tenure;
    private Integer activePeriods;
    private Integer defaultPeriods;
    private Double installment;
    private Double revenueReceived;
    private Double interestReceived;
    private Double outstandingBalance;
    private Double reversalIfTerminated;
    private Integer noOfTimesRedeployed;
    private List<String> lastSevenDaysDates;
    private List<Object> lastSevenDaysData;

}
