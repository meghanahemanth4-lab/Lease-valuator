package com.spinteg.lv.dto;

import java.util.UUID;

import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.entity.enums.VehicleStatus;
import lombok.Data;

@Data
public class CreateContractTypeDTO {
    
    private String contractName;
    private String contractCode;

    private String tenure;
    private String installment;
    private String downPayment;
    private Frequency paymentFrequency;
    private String typeOfVehicle;
}
