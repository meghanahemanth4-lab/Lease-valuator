package com.spinteg.lv.entity;

import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lease_contract", schema = "contract")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaseContract {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "contract_no")
    private UUID contractNo;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "vehicle_no")
    private String vehicleNo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_number")
    private String customerNumber;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private LeaseContractStatus status = LeaseContractStatus.ACTIVE;

    @Column(name = "revenue_pending")
    private BigDecimal revenuePending;

    @Column(name = "value_of_lease_today")
    private BigDecimal valueOfLeaseToday;

    @ManyToOne
    @JoinColumn(name = "contract_type_id", referencedColumnName = "contract_type_id")
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "vehicle_status", columnDefinition = "contract.vehicle_status")
    private VehicleStatus vehicleStatus;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "country", length = 50)
    private String country;

    @OneToMany(mappedBy = "leaseContract", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<AmortizationEntry> amortizationEntries;

    @Column(name = "number_of_times_redeployed")
    private Integer noOfTimesRedeployed;

    @Column(name = "sum_of_pv_of_cf")
    private BigDecimal sumOfPvOfCf;

    @Column(name = "downpayment_received")
    private BigDecimal downpaymentReceived;

    @Column(name = "downpayment_trans_date")
    private LocalDate downpaymentTransDate;

}
