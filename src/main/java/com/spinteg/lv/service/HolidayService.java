package com.spinteg.lv.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataNotFoundException;

@Service
public class Holidayservice {

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

        if (dates.isEmpty() == true)
            throw new DataNotFoundException("No holidays found for the given country");

        return dates;
    }
}
