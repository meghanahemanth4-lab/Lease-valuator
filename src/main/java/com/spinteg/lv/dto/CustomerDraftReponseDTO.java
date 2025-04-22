package com.spinteg.lv.dto;

import lombok.Data;

@Data
public class CustomerDraftReponseDTO {
    private String contractTypeId;
    private String startDate;
    private String customerName;
    private String customerNumber;
    private String vehicleStatus;
    private String country;
    private String city;
    private Integer noOfTimesDeployed;
    private String vehicleNumber;
}
