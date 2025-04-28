package com.spinteg.lv.service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class DateMatcher {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM");

    public static boolean isMatchingDate(LocalDate localDate, String holidayDate) {
        String cleanDate = holidayDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");

        MonthDay holidayMonthDay = MonthDay.parse(cleanDate, formatter);

        MonthDay todayMonthDay = MonthDay.from(localDate);

        return holidayMonthDay.equals(todayMonthDay);
    }

}