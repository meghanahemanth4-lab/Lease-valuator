package com.spinteg.lv.dto;

import lombok.Data;

@Data
public class UpdateContractTypeDTO {
    private String baseRent;
    private String insuranceAmount;
    private String maintenanceAmount;
    private String swapAmount;
}
