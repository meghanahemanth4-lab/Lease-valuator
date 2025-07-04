package com.spinteg.lv.service;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

@Component
public class DateMatcher {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM");
    private static final DateTimeFormatter FORMATTER_PARAM = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static boolean isMatchingDate(LocalDate localDate, String holidayDate) {

        String cleanDate = holidayDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");

        MonthDay holidayMonthDay = MonthDay.parse(cleanDate, FORMATTER);

        MonthDay todayMonthDay = MonthDay.from(localDate);

        return holidayMonthDay.equals(todayMonthDay);
    }

    public static boolean isMatchingDateOrNot(LocalDate localDate, String holidayDate) 
    {
        if (!isValidDateFormat(holidayDate)) {
            return isMatchingDate(localDate, holidayDate);
        }
        LocalDate holiLocalDate = LocalDate.parse(holidayDate, FORMATTER_PARAM);
        return localDate.equals(holiLocalDate);
    }

    public static boolean isValidDateFormat(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}