package com.spinteg.lv.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Integer noOfTimesRedeployed=0;

}
