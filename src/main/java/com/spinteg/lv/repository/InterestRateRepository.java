package com.spinteg.lv.repository;

import com.spinteg.lv.entity.InterestRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRateRepository extends JpaRepository<InterestRate, Long> {
}