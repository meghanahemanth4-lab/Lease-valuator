package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.entity.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractTypeDTO {

    private String contractTypeId;
    private Integer tenure;
    private float interestRate;
    private float installment;
    private Frequency paymentFrequency;
    private Deployment deployment;
    private VehicleType typeOfVehicle;
    private Integer baseRent;
    private float maintenanceAmount;
    private float insuranceAmount;
    private float downpayment;
}
