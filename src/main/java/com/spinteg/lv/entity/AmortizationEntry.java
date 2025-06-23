package com.spinteg.lv.entity;

import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "amortization_entry",
    schema = "contract",
    indexes = {
        @Index(name = "idx_transaction_date", columnList = "transaction_date")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmortizationEntry {

    @Id
    @Column(name = "amortization_entry_id", length = 50)
    @Builder.Default
    private UUID amortizationEntryId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lease_contract", nullable = false) 
    private LeaseContract leaseContract;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "period")
    private Long period;

    @Column(name = "installment")
    private Double installment;

    @Column(name = "base_rent")
    private Double baseRent;

    @Column(name = "insurance")
    private Double insurance;

    @Column(name = "maintenance")
    private Double maintenance;

    @Column(name = "cumulative_installment")
    private Double cumulativeInstallment;

    @Column(name = "closing_balance")
    private Double closingBalance;

    @Column(name = "outstanding_balance")
    private Double outstandingBalance;

    @Column(name = "interest_component")
    private Double interestComponent;

    @Column(name = "principal_component")
    private Double principalComponent;

    @Column(name = "cumulative_principal")
    private Double cumulativePrincipal;

    @Column(name = "cumulative_interest")
    private Double cumulativeInterest;

    @Column(name = "remaining_amount")
    private Double remainingAmount;

    @Column(name = "pv_of_cf")
    private Double pvOfCf;

    @Column(name = "lease_payment_status")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private LeasePaymentStatus leasePaymentStatus = LeasePaymentStatus.PENDING;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "swap")
    private Double swap;
}
