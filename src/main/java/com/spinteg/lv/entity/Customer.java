package com.spinteg.lv.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer", schema = "contract")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @Column(name = "customer_number", length = 50, nullable = false)
    private String customerNumber;

    @Column(name = "customer_name", length = 100, nullable = false)
    private String customerName;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "contract_draft_id", referencedColumnName = "contract_draft_id", unique = true)
    private CustomerContractDraft customerContractDraft;

    @Column(name = "is_lease_contract")
    private boolean isLeaseContract;
}
