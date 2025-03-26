package com.spinteg.lv.repository;

import com.spinteg.lv.entity.ContractType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, String> {

    @Query("SELECT ct.contractTypeId FROM ContractType ct")
    List<String> findAllContractTypeIds();

    Optional<ContractType> findByContractTypeId(String contractTypeId);

    List<ContractType> findByBaseRentIsNull();
}
