package com.spinteg.lv.exception;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DataValidation {

    public static String isNumeric(String str, String field) {
        try {
            Double.parseDouble(str.trim());
            return str.trim();
        } catch (NumberFormatException e) {
            throw new CustomDataMissingException(field + " should be a number");
        }
    }

    public static String isInteger(String str, String field) {
        try {
            Integer.parseInt(str.trim());
            return str.trim();
        } catch (NumberFormatException e) {
            throw new CustomDataMissingException(field + " should be an integer");
        }
    }

    public static String isNotZero(String str, String field) {
        if (Double.parseDouble(str.trim()) == 0)
            throw new CustomDataMissingException(field + " should not be zero");
        return str.trim();
    }

    public static String isNotNegative(String str, String field) {
        if (Integer.parseInt(str.trim()) <= 0)
            throw new CustomDataMissingException(field + " should not be zero");
        return str.trim();
    }

    public static String isNotNegativeAndZero(String str, String field) {
        if (Double.parseDouble(str.trim()) < 0)
            throw new CustomDataMissingException(field + " should not be negative and zero");
        return str.trim();
    }

    public static String isIntegerAndNotZero(String str, String field) {
        if (Integer.parseInt(str.trim()) < 0)
            throw new CustomDataMissingException(field + " should be an integer and not zero");
        return str.trim();
    }

    public static String isValidDate(String str, String field) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(str.trim(), formatter); // Ensures a valid date
            return str.trim();
        } catch (DateTimeParseException e) {
            throw new CustomDataMissingException(field + " should be in YYYY-MM-DD format");
        }
    }
}
