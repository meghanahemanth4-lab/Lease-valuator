package com.spinteg.lv.dto;

import java.time.LocalDate;
import java.util.UUID;

import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerInputDTO {
    private String customerNumber;
    private String contractCode;

    private String customerName;

    private String country;
    private String city;
    private String phoneNumber;
    @Nullable
    private LocalDate startDate;
    @Nullable
    private LocalDate endDate;
    private String contractTypeId;
}
