package com.spinteg.lv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.LeaseContractRepository;

@Service
public class SchedulerService {

    @Autowired
    private AmortizationEntryRepository amortizationEntryRepository;

    @Autowired
    private LeaseContractRepository leaseContractRepository;

    @Scheduled(cron = "0 00 11 * * ?")
    public void updatePendingPaymentsToDefaulted() {
        leaseContractRepository.updatePendingPaymentsToDefaulted();
    }

    @Scheduled(cron = "0 30 11 * * ?")
    public void updateAmortizationEntriesForToday() {
        leaseContractRepository.updateAmortizationEntriesForToday();
    }
}
