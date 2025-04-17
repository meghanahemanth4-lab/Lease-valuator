package com.spinteg.lv.controller;

import com.spinteg.lv.dto.DownpaymentDTO;
import com.spinteg.lv.dto.PaymentApiDTO;
import com.spinteg.lv.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/rent")
    public ResponseEntity<?> updatePaymentStatus(@RequestBody PaymentApiDTO paymentApiDTO) {
        return paymentService.updatePaymentStatusToPaid(paymentApiDTO);
    }

    // @PostMapping("/downpayment")
    // public ResponseEntity<?> updateDownPaymentStatus(@RequestBody DownpaymentDTO
    // downpaymentDTO) {
    // return paymentService.updateDownPaymentStatusToPaid(downpaymentDTO);
    // }
    @PostMapping("/downpayment")
    public ResponseEntity<?> updateDownPaymentStatus(@RequestBody DownpaymentDTO downpaymentDTO) {
        return paymentService.updatePaymentStatustobePaid(downpaymentDTO);
    }
}
