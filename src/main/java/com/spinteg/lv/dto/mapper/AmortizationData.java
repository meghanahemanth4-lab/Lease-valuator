package com.spinteg.lv.dto.mapper;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AmortizationData {
    private LocalDate date;
    private String installment;

}
