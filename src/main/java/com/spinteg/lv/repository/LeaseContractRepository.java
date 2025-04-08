package com.spinteg.lv.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;

@Repository
public interface LeaseContractRepository extends JpaRepository<LeaseContract, UUID> {
        List<LeaseContract> findByStatusIn(List<LeaseContractStatus> status);

        List<LeaseContract> findByStatus(LeaseContractStatus status);

        List<LeaseContract> findByCustomerNumber(String customerNumber);

        @Query(value = "SELECT * FROM contract.lease_contract lc WHERE lc.customer_number = :customerNumber AND lc.contract_type_id = :contractTypeId", nativeQuery = true)
        LeaseContract findByCustomerIdAndContractTypeId(String customerNumber, String contractTypeId);

        @Query(value = """
                        SELECT SUM(ae.base_rent)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND ae.lease_payment_status = 'PAID'
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumBaseRentByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(ae.maintenance)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND ae.lease_payment_status = 'PAID'
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumMaintenanceByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(ae.interest_component)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND ae.lease_payment_status = 'PAID'
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumInterestComponentByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(ae.insurance)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND ae.lease_payment_status = 'PAID'
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumInsuranceByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(ae.interest_component)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND (ae.lease_payment_status = 'DEFAULTED' OR
                        ae.lease_payment_status = 'NO_RENT_DAY')
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumInterestByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(ae.principal_component)
                        FROM contract.lease_contract lc
                        JOIN contract.amortization_entry ae
                        ON lc.contract_no = ae.lease_contract
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND (ae.lease_payment_status = 'DEFAULTED' OR
                        ae.lease_payment_status = 'NO_RENT_DAY')
                        AND EXTRACT(MONTH FROM ae.date) = :month
                        AND EXTRACT(YEAR FROM ae.date) = :year
                        """, nativeQuery = true)
        Float sumBikeSaleFinanceLeaseDebtorDr(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

        @Query(value = """
                        SELECT SUM(lc.sum_of_pv_of_cf)
                        FROM contract.lease_contract lc
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        """, nativeQuery = true)
        Float sumPvOfCfByDeploymentAndVehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType);

        @Query(value = """
                        UPDATE contract.amortization_entry ae
                        SET lease_payment_status = 'NO_RENT_DAY'
                        FROM contract.lease_contract lc
                        WHERE ae.lease_contract = lc.contract_no
                        AND ae.date = CURRENT_DATE
                        AND ae.lease_payment_status = 'NO_RENT_DAY_FUTURE'
                        AND lc.status IN ('ACTIVE', 'DEFAULTED')
                        """, nativeQuery = true)
        void updateAmortizationEntriesForToday(); 
        @Modifying
        @Transactional
        @Query(value = """
                        UPDATE contract.amortization_entry ae
                        SET lease_payment_status = 'DEFAULTED'
                        FROM contract.lease_contract lc
                        WHERE ae.lease_contract = lc.contract_no
                        AND ae.date = CURRENT_DATE
                        AND ae.lease_payment_status = 'PENDING'
                        AND lc.status IN ('ACTIVE', 'DEFAULTED')
                        """, nativeQuery = true)
        void updatePendingPaymentsToDefaulted();

        @Query(value = """
                        SELECT SUM(lc.downpayment_received)
                        FROM contract.lease_contract lc
                        JOIN contract.contract_type ct
                        ON lc.contract_type_id = ct.contract_type_id
                        WHERE ct.deployment = CAST(:deployment AS contract.deployment)
                        AND ct.type_of_vehicle = CAST(:vehicleType AS contract.vehicle_type)
                        AND EXTRACT(MONTH FROM lc.downpayment_trans_date) = :month
                        AND EXTRACT(YEAR FROM lc.downpayment_trans_date) = :year
                        """, nativeQuery = true)
        Float sumofDownpaymentReceivedByDeploymentAndVsehicleTypeAndMonthAndYear(
                        String deployment,
                        String vehicleType,
                        int month,
                        int year);

}
