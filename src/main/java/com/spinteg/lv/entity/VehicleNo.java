package com.spinteg.lv.entity;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_no", schema = "contract")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleNo {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "vehicleNoId", updatable = false, nullable = false)
    private UUID vehicleNoId;

    @Column(name = "vehicle_no", length = 50, nullable = true)
    private String vehicleNo;

    @Column(name = "number_of_times_deployed")
    private Integer noOfTimesDeployed;

    @Column(name = "is_deployed")
    @Builder.Default
    private boolean isDeployed = false;

}
