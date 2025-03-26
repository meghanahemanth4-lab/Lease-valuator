package com.spinteg.lv.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VehicleType {
    COMMANDO("COMMANDO"),
    EKON("EKON"),
    M_ELECTRIC("M-Electric"),
    PIAM("PIAM"),
    SPIROVEO("SPIROVEO"),
    CHAP_CHAP("CHAP CHAP");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VehicleType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle Type cannot be null or empty");
        }

        String normalizedValue = value.trim().replace("_", "-"); // Normalize underscores to hyphens

        for (VehicleType e : values()) {
            if (e.value.equalsIgnoreCase(normalizedValue)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid value in vehicle Type: " + value);
    }
}
