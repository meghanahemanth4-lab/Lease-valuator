package com.spinteg.lv.service;

import com.spinteg.lv.ExceptionHandling.DataNotFoundException;
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

    public InterestRate setInterestRate(float rate) {
        InterestRate interestRate = new InterestRate();
        interestRate.setId(1L);
        interestRate.setRate(rate);
        return interestRateRepository.save(interestRate);
    }
}