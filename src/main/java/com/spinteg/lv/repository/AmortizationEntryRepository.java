package com.spinteg.lv.repository;

import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AmortizationEntryRepository extends JpaRepository<AmortizationEntry, UUID> {
    List<AmortizationEntry> findByDateAndLeasePaymentStatus(LocalDate date, LeasePaymentStatus status);
}
