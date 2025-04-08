package com.spinteg.lv.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.entity.enums.VehicleStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateContractTypeDTO {

    private String contractName;
    private String contractCode;

    private String tenure;
    private String installment;
    private String downPayment;

    @Schema(description = "Payment frequency", defaultValue = "DAILY")
    @JsonSetter(nulls = Nulls.SKIP)
    private String paymentFrequency = Frequency.DAILY.toString();
    private String typeOfVehicle;

    @Schema(description = "Deployment", defaultValue = "LEASING")
    @JsonSetter(nulls = Nulls.SKIP)
    private String deployment = Deployment.LEASING.toString();

    public String getDeployment() {
        return deployment == null ? Deployment.LEASING.toString() : deployment;
    }

    public String getPaymentFrequency() {
        return paymentFrequency == null ? Frequency.DAILY.toString() : paymentFrequency;
    }

}
