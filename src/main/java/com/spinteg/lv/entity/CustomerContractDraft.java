package com.spinteg.lv.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_contract_draft", schema = "contract")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContractDraft {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "contract_draft_id", updatable = false, nullable = false)
    private UUID contractDraftId;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "contract_type_id")
    private String contractTypeId;

    @Column(name = "contract_code")
    private String contractCode;

}
