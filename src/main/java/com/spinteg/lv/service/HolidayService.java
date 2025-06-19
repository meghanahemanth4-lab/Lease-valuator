package com.spinteg.lv.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataNotFoundException;

import ch.qos.logback.core.joran.conditional.ElseAction;

@Service
public class HolidayService {

    public List<String> getHolidaysDatesForCountry(String country) {
        List<String> dates = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("HolidaysPoint.json");

            if (inputStream == null) {
                throw new FileNotFoundException("HolidaysPoint.json not found in resources folder");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode countryNode = rootNode.get(country.trim().toLowerCase());
            if (countryNode == null || countryNode.isNull()) {
                throw new CustomDataMissingException("Country not found: " + country);
            }
            Iterator<Map.Entry<String, JsonNode>> fields = countryNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                dates.add(entry.getKey());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if (dates.isEmpty() == true)
        // throw new DataNotFoundException("No holidays found for the given country");

        return dates;
    }

    public List<String> getHolidaysDatesForCountryAndYear(String country) {

        List<String> dates = new ArrayList<>();
        try {

            JsonNode countryNode = isCountryExists(country);

            Iterator<Map.Entry<String, JsonNode>> fieldsWithYears = countryNode.fields();

            List<String> tempDates = new ArrayList<>();
            while (fieldsWithYears.hasNext()) {
                Map.Entry<String, JsonNode> entryWithHoliday = fieldsWithYears.next();

                String year = entryWithHoliday.getKey();
                JsonNode nodeWithHolidays = entryWithHoliday.getValue();

                Iterator<Map.Entry<String, JsonNode>> holidayList = nodeWithHolidays.fields();

                while (holidayList.hasNext()) {
                    Map.Entry<String, JsonNode> holidayEntry = holidayList.next();
                    String holidayDate = holidayEntry.getKey();
                    // JsonNode holidayNameNode = holidayEntry.getValue();
                    String formattedDate = convertToDate(holidayDate, Integer.parseInt(year));
                    tempDates.add(formattedDate);
                }
                if (tempDates.size() == 0) {
                    dates.addAll(createTheDefaultHolidaysForthatYear(country, year));
                } else {
                    dates.addAll(tempDates);
                    tempDates.clear();
                }
            }
            System.out.println(dates);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dates.isEmpty() == true) {
            // Modify this line depending on your requirements because if no holidays are
            // found, you might want to return an default country holiday list.
            return getHolidaysDatesForCountry(country);
        }
        return dates;
    }

    public JsonNode isCountryExists(String country) {

        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("HolidayCalenderMain.json");

            if (inputStream == null) {
                throw new FileNotFoundException("HolidayCalenderMain.json not found in resources folder");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode countryNode = rootNode.get(country.trim().toLowerCase());
            if (countryNode == null || countryNode.isNull()) {
                throw new CustomDataMissingException("Country not found: " + country);
            }
            return countryNode;
        } catch (IOException e) {
            throw new DataNotFoundException("Error reading the holiday data for country: " + country);
        }
    }

    private List<String> createTheDefaultHolidaysForthatYear(String country, String year) {

        List<String> defaultDatesForThatCountry = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("HolidaysPoint.json");

            if (inputStream == null) {
                throw new FileNotFoundException("HolidaysPoint.json not found in resources folder");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode countryNode = rootNode.get(country.trim().toLowerCase());
            if (countryNode == null || countryNode.isNull()) {
                throw new CustomDataMissingException("Country not found in : " + country);
            }
            Iterator<Map.Entry<String, JsonNode>> holidayList = countryNode.fields();

            while (holidayList.hasNext()) {
                Map.Entry<String, JsonNode> holidayEntry = holidayList.next();
                String holidayDate = holidayEntry.getKey();
                // JsonNode holidayNameNode = holidayEntry.getValue();
                String formattedDate = convertToDate(holidayDate, Integer.parseInt(year));
                defaultDatesForThatCountry.add(formattedDate);
            }

        } catch (IOException e) {
            System.out.println("Default holiday list having some issues");
        }
        return defaultDatesForThatCountry;
    }

    public static String convertToDate(String dayMonth, int year) {

        String cleanedDayMonth = dayMonth.replaceAll("(?i)\\b([1-9]|[12][0-9]|3[01])(st|nd|rd|th)\\b", "$1").trim();
        String dateStr = cleanedDayMonth + " " + year;

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        return date.format(outputFormatter);
    }
}
