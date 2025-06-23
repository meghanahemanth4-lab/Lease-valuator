package com.spinteg.lv.service;

import com.spinteg.lv.dto.AccountingEntryDTO;
import com.spinteg.lv.dto.AccountingEntryUpdate;
import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.ValidationUtil;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.entity.enums.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountingEntryService {

    @Autowired
    private LeaseContractRepository leaseContractRepository;    
    
    public AccountingEntryDTO getAccountingEntry(AccountingEntryUpdate update) {
        // Validate input
        if (update == null) {
            throw new CustomDataMissingException("AccountingEntryUpdate cannot be null");
        }
        if (update.getDeployment() == null || update.getDeployment().name().isEmpty()) {
            throw new CustomDataMissingException("Deployment cannot be null or empty");
        }
        if (update.getVehicleType() == null || update.getVehicleType().isEmpty()) {
            throw new CustomDataMissingException("VehicleType cannot be null or empty");
        }
        if (update.getMonth() == null || update.getMonth() < 1 || update.getMonth() > 12) {
            throw new CustomDataMissingException("Month must be between 1 and 12");
        }
        if (update.getYear() == null || update.getYear() < 1900) {
            throw new CustomDataMissingException("Year must be a valid year");
        }

        // Process the request
        AccountingEntryDTO dto = new AccountingEntryDTO();
        dto.setLeaseReceivableDr(leaseContractRepository.sumPvOfCfByDeploymentAndVehicleTypeAndMonthAndYear(
                update.getDeployment().name(),
                VehicleType.fromValue(update.getVehicleType()).name(),
                update.getMonth(),
                update.getYear()));
        dto.setBikeSaleFinanceLeaseCr(dto.getLeaseReceivableDr());
        dto.setRentalCollectionWalletDr(
                leaseContractRepository.sumBaseRentByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        
                        update.getMonth(),
                        update.getYear()));
        dto.setRentalIncomeCr(dto.getRentalCollectionWalletDr());
        dto.setMaintenanceCollectionWalletDr(leaseContractRepository
                .sumMaintenanceByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setMaintenanceIncomeCr(dto.getMaintenanceCollectionWalletDr());
        dto.setInsuranceIncomeCollectionWalletDr(leaseContractRepository
                .sumInsuranceByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setInsuranceIncomeCr(dto.getInsuranceIncomeCollectionWalletDr());
        dto.setInterestIncomeLeaseReceivableDr(leaseContractRepository
                .sumInterestComponentByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setInterestIncomeCr(dto.getInterestIncomeLeaseReceivableDr());
        dto.setLeaseReceivableInterestIncomeDr(leaseContractRepository
                .sumInterestByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setLeaseReceivableInterestIncomeCr(dto.getLeaseReceivableInterestIncomeDr());
        dto.setBikeSaleFinanceLeaseDr(dto.getRentalCollectionWalletDr());
        dto.setLeaseReceivableCr(dto.getBikeSaleFinanceLeaseDr());
        dto.setSwapCollectionWalletDr(
                leaseContractRepository.sumSwapByDeploymentAndVehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()
                )
        );
        dto.setSwapRevenueCr(dto.getSwapCollectionWalletDr());
        dto.setTvaCr(0f);
        dto.setDownpaymentCollectionWalletDr(leaseContractRepository
                .sumofDownpaymentReceivedByDeploymentAndVsehicleTypeAndMonthAndYear(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setDownpaymentIncomeCr(dto.getDownpaymentCollectionWalletDr());
        dto.setServiceCollectionWalletDr(0f);
        dto.setServiceIncomeCr(0f);
        dto.setBikeSaleFinanceLeaseDebtorDr(
                leaseContractRepository.sumBikeSaleFinanceLeaseDebtorDr(
                        update.getDeployment().name(),
                        VehicleType.fromValue(update.getVehicleType()).name(),
                        update.getMonth(),
                        update.getYear()));
        dto.setLeaseReceivableFinanceDebtorCr(dto.getBikeSaleFinanceLeaseDebtorDr());

        return dto;
    }
}