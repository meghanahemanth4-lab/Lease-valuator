package com.spinteg.lv.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
public class CreateLeaseContractDTO {

    @NotNull
    private String contractTypeId;

    @NotNull
    private LocalDate startDate;

    private String customerName;

    @NotNull
    private String customerNumber;

    @NotNull
    private String vehicleStatus;

    @NotNull
    private String country;

    @NotNull
    private String city;
    
    @Builder.Default
    private Integer noOfTimesDeployed = 1;
    
    @Null
    @Builder.Default
    private String vehicleNumber = null;

}
