package com.spinteg.lv.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "rent_payment",
    schema = "contract",
    indexes = {
        @Index(name = "rent_payment_date", columnList = "date"),
        @Index(name = "rent_payment_transaction_date", columnList = "transaction_date")
    }
)
@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor
public class RentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_number", nullable = false)
    private String customerNumber;

    @ManyToOne
    @JoinColumn(name = "contract_type_id", referencedColumnName = "contract_type_id", nullable = false)
    private ContractType contractType;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate trasactionDate;
}
