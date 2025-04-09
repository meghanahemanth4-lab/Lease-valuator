package com.spinteg.lv.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import com.spinteg.lv.entity.enums.VehicleType;
import com.spinteg.lv.entity.enums.Deployment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "downpayments", indexes = @Index(columnList = "transaction_date"))
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Downpayment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "contract_type_id")
    private String contractTypeId;
    
    @Column(name = "payment_amount")
    private BigDecimal paymentAmount;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "vehicle_type", columnDefinition = "vehicle_type")
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "deployment", columnDefinition = "deployment")
    private Deployment deployment;
}