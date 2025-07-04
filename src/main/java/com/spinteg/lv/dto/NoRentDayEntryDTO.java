package com.spinteg.lv.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class NoRentDayEntryDTO {
    LocalDate date;
    String country;
}
