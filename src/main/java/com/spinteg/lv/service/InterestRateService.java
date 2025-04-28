package com.spinteg.lv.service;

import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataNotFoundException;
import com.spinteg.lv.entity.InterestRate;
import com.spinteg.lv.repository.InterestRateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InterestRateService {

    private final InterestRateRepository interestRateRepository;

    public InterestRate getInterestRate() {
        return interestRateRepository.findById(1L)
                .orElseThrow(() -> new DataNotFoundException("Interest rate not found"));
    }

    public InterestRate setInterestRate(String rate) {
        checkInterestRate(rate);
        InterestRate interestRate = new InterestRate();
        interestRate.setId(1L);
        interestRate.setRate(Float.parseFloat(rate));
        return interestRateRepository.save(interestRate);
    }

    public void checkInterestRate(String rate) {
        try {
            Float.parseFloat(rate);
        } catch (NumberFormatException e) {
            throw new CustomDataMissingException("Invalid interest rate: " + rate);
        }
        if (Float.parseFloat(rate) < 0 || Float.parseFloat(rate) > 100) {
            throw new CustomDataMissingException("Interest rate should be between 0 and 100");
        }
    }
}