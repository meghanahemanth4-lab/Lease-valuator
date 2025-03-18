package com.spinteg.lv.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.time.DayOfWeek;

@Data
public class DateCalculator {

    private LocalDate startDate;
    private int period;  // Number of valid days to count, excluding Sundays
    private LocalDate endDate;
    private int numberOfSundays;
    private int totalDays;

    // Constructor that initializes startDate and period, and calculates endDate, number of Sundays, and totalDays
    public DateCalculator(LocalDate startDate, int period) {
        this.startDate = startDate;
        this.period = period;
        calculateEndDate();
    }

    // Method to calculate the end date, number of Sundays, and total days
    private void calculateEndDate() {
        LocalDate currentDate = startDate;
        int validDaysCount = 0;
        numberOfSundays = 0;
        totalDays = 0;

        // Loop until we reach the required number of valid days
        while (validDaysCount < period) {
            currentDate = currentDate.plusDays(1);
            totalDays++;

            if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                numberOfSundays++;
            } else {
                validDaysCount++;
            }
        }

        // Set the endDate to the final calculated date
        this.endDate = currentDate;
    }
  

}
