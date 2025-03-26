package com.spinteg.lv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interest_rate", schema = "contract")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestRate {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "rate")
    private float rate;
}