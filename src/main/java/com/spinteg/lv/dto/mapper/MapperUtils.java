package com.spinteg.lv.dto.mapper;

import com.spinteg.lv.dto.AmortizationEntryDto;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class MapperUtils {
    public static final List<LeasePaymentStatus> activeStatuses = List.of(LeasePaymentStatus.PAID, LeasePaymentStatus.DEFAULTED, LeasePaymentStatus.NO_RENT_DAY);
    public static final List<LeasePaymentStatus> defaultStatuses = List.of(LeasePaymentStatus.DEFAULTED);
    public static final List<LeasePaymentStatus> paidStatuses = List.of(LeasePaymentStatus.PAID, LeasePaymentStatus.NO_RENT_DAY);
    public static final List<LeasePaymentStatus> defaultAndNoRentDay = List.of(LeasePaymentStatus.DEFAULTED, LeasePaymentStatus.NO_RENT_DAY);

    public static List<AmortizationEntryDto> getSchedulesByStatus(List<AmortizationEntryDto> amortizationSchedules, List<LeasePaymentStatus> statuses) {
        return amortizationSchedules.stream().filter(amortizationEntryDto -> statuses.contains(amortizationEntryDto.getLeasePaymentStatus())).toList();
    }
    public static BigDecimal sumByField(List<AmortizationEntryDto> entries, Function<AmortizationEntryDto, Double> fieldGetter) {
        return BigDecimal.valueOf(entries.stream()
                .map(fieldGetter)
                .filter(value -> value != null) // Handle potential nulls
                .reduce(Double::sum)
                .orElse(0.0));
    }
}
