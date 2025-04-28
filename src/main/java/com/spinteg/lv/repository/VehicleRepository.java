package com.spinteg.lv.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spinteg.lv.entity.VehicleNo;

public interface VehicleRepository extends JpaRepository<VehicleNo, UUID> {
    List<VehicleNo> findByVehicleNo(String vehicleNo);
}
