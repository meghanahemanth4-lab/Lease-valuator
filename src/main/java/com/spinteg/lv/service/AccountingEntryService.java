package com.spinteg.lv.service;

import com.spinteg.lv.dto.AccountingEntryDTO;
import com.spinteg.lv.dto.AccountingEntryUpdate;
import com.spinteg.lv.repository.LeaseContractRepository;
import com.spinteg.lv.entity.enums.VehicleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

@Service
public class AccountingEntryService {

        @Autowired
        private LeaseContractRepository leaseContractRepository;

    public AccountingEntryDTO getAccountingEntry(AccountingEntryUpdate update) {
        AccountingEntryDTO dto = new AccountingEntryDTO();
        dto.setLeaseReceivableDr(leaseContractRepository.sumPvOfCfByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(),VehicleType.fromValue(update.getVehicleType()).name()));
        dto.setBikeSaleFinanceLeaseCr(dto.getLeaseReceivableDr());
        dto.setRentalCollectionWalletDr(leaseContractRepository.sumBaseRentByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(), VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(),update.getYear()));
        dto.setRentalIncomeCr(dto.getRentalCollectionWalletDr());
        dto.setMaintenanceCollectionWalletDr(leaseContractRepository.sumMaintenanceByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(),VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(), update.getYear()));
        dto.setMaintenanceIncomeCr(dto.getMaintenanceCollectionWalletDr());
        dto.setInsuranceIncomeCollectionWalletDr(leaseContractRepository.sumInsuranceByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(),VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(), update.getYear()));
        dto.setInsuranceIncomeCr(dto.getInsuranceIncomeCollectionWalletDr());
        dto.setInterestIncomeLeaseReceivableDr(leaseContractRepository.sumInterestComponentByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(),VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(), update.getYear()));
        dto.setInterestIncomeCr(dto.getInterestIncomeLeaseReceivableDr());
        dto.setLeaseReceivableInterestIncomeDr(leaseContractRepository.sumInterestByDeploymentAndVehicleTypeAndMonthAndYear(update.getDeployment().name(), VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(), update.getYear()));
        dto.setLeaseReceivableInterestIncomeCr(dto.getLeaseReceivableInterestIncomeDr());
        dto.setBikeSaleFinanceLeaseDr(dto.getRentalCollectionWalletDr());
        dto.setLeaseReceivableCr(dto.getBikeSaleFinanceLeaseDr());
        dto.setSwapCollectionWalletDr(0f);
        dto.setSwapRevenueCr(0f);
        dto.setTvaCr(0f);
        dto.setDownpaymentCollectionWalletDr(0f);
        dto.setDownpaymentIncomeCr(0f);
        dto.setServiceCollectionWalletDr(0f);
        dto.setServiceIncomeCr(0f);
        dto.setBikeSaleFinanceLeaseDebtorDr(leaseContractRepository.sumBikeSaleFinanceLeaseDebtorDr(update.getDeployment().name(),VehicleType.fromValue(update.getVehicleType()).name(), update.getMonth(), update.getYear()));
        dto.setLeaseReceivableFinanceDebtorCr(dto.getBikeSaleFinanceLeaseDebtorDr());
        return dto;
    }
}