package com.spinteg.lv.controller;

import com.spinteg.lv.dto.DownpaymentDTO;
import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Mark rent paid -> LEASE_CREATOR + SUPER_ADMIN
    @PostMapping("/rent")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody PaymentApiDTO paymentApiDTO) {
        return paymentService.updatePaymentStatusToPaid(paymentApiDTO);
    }

    // Mark downpayment paid -> LEASE_CREATOR + SUPER_ADMIN
    @PostMapping("/downpayment")
    @PreAuthorize("hasAnyAuthority('ROLE_SUPER_ADMIN','ROLE_LEASE_CREATOR')")
    public ResponseEntity<?> updateDownPaymentStatus(@RequestBody DownpaymentDTO downpaymentDTO) {
        return paymentService.updatePaymentStatustobePaid(downpaymentDTO);
    }
}
