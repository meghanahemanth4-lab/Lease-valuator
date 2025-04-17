package com.spinteg.lv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spinteg.lv.entity.Downpayment;

@Repository
public interface DownPaymentRepository extends JpaRepository<Downpayment, Long> {

    Downpayment findByCustomerNumber(String customerNumber);

}
