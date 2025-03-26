package com.spinteg.lv.service;

import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.AmortizationEntry;

import java.util.List;

public interface LeaseScheduleCalculator {
    List<AmortizationEntry> generateAmortizationSchedule(LeaseContract leaseContract,List<String> list);
}
