package com.spinteg.lv.entity;

import java.util.UUID;

import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.entity.enums.VehicleStatus;
import com.spinteg.lv.entity.enums.VehicleType;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Table(name = "contract_type", schema = "contract")
@Data
public class ContractType {
    @Id
    @Column(name = "contract_type_id", length = 100)
    private String contractTypeId;

    @Column(name = "contract_code")
    private String ContractCode;

    @Column(name = "tenure")
    private Integer tenure;

    @Column(name = "interest_rate")
    private float interestRate;

    @Column(name = "installment")
    private Float installment;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "payment_frequency", columnDefinition = "contract.frequency")
    private Frequency paymentFrequency;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "deployment", columnDefinition = "contract.deployment")
    private Deployment deployment;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type_of_vehicle", columnDefinition = "contract.vehicle_type")
    private VehicleType typeOfVehicle;

    @Column(name = "base_rent")
    private Integer baseRent;

    @Column(name = "maintenance_amount")
    private Float maintenanceAmount;

    @Column(name = "insurance_amount")
    private Float insuranceAmount;

    @Column(name = "downpayment")
    private float downpayment;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "vehicle_status", columnDefinition = "contract.vehicle_status")
    private VehicleStatus vehicleStatus;

    @Column(name = "swap_amount")
    private Float swapAmount;

}
