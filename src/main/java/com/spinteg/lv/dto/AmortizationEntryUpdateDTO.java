package com.spinteg.lv.dto;

import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AmortizationEntryUpdateDTO {
    UUID amortizationEntryId;
    LeasePaymentStatus status;
}
