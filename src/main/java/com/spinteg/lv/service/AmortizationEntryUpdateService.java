package com.spinteg.lv.service;

import com.spinteg.lv.dto.AmortizationEntryUpdateDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.LeaseContractRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AmortizationEntryUpdateService {
        private final AmortizationEntryRepository amortizationEntryRepository;
        private final LeaseContractRepository leaseContractRepository;

        public boolean updateAmortizationEntry(UUID amortizationEntryId, LeasePaymentStatus status) {
                Optional<AmortizationEntry> amortizationEntry = amortizationEntryRepository
                                .findById(amortizationEntryId);
                if (amortizationEntry.isPresent()) {
                        val amortizationEntryToUpdate = amortizationEntry.get();
                        val statusBeforeUpdate = amortizationEntryToUpdate.getLeasePaymentStatus();
                        amortizationEntryToUpdate.setLeasePaymentStatus(status);
                        amortizationEntryRepository.saveAndFlush(amortizationEntryToUpdate);

                        if (statusBeforeUpdate.equals(LeasePaymentStatus.DEFAULTED)
                                        && status.equals(LeasePaymentStatus.PAID)) {
                                correctLeaseStatusIfRequired(amortizationEntryToUpdate.getLeaseContract(),
                                                amortizationEntryToUpdate.getLeaseContract().getAmortizationEntries(),
                                                LeasePaymentStatus.DEFAULTED);
                        }
                        if (statusBeforeUpdate.equals(LeasePaymentStatus.TERMINATED)
                                        && status.equals(LeasePaymentStatus.PAID)) {
                                correctLeaseStatusIfRequired(amortizationEntryToUpdate.getLeaseContract(),
                                                amortizationEntryToUpdate.getLeaseContract().getAmortizationEntries(),
                                                LeasePaymentStatus.TERMINATED);
                        }
                        if (statusBeforeUpdate.equals(LeasePaymentStatus.SHORT_CLOSE)
                                        && status.equals(LeasePaymentStatus.PAID)) {
                                correctLeaseStatusIfRequired(amortizationEntryToUpdate.getLeaseContract(),
                                                amortizationEntryToUpdate.getLeaseContract().getAmortizationEntries(),
                                                LeasePaymentStatus.SHORT_CLOSE);
                        }
                        updateLeaseStatusIfRequired(amortizationEntryToUpdate.getLeasePaymentStatus(),
                                        amortizationEntryToUpdate.getLeaseContract().getStatus(),
                                        amortizationEntryToUpdate.getLeaseContract());
                        return true;
                } else {
                        return false;
                }
        }

        private void correctLeaseStatusIfRequired(LeaseContract leaseContract,
                        List<AmortizationEntry> amortizationEntries,
                        LeasePaymentStatus correctFrom) {
                if (amortizationEntries.stream()
                                .noneMatch(amortizationEntry -> amortizationEntry.getLeasePaymentStatus()
                                                .equals(correctFrom))) {
                        leaseContract.setStatus(LeaseContractStatus.ACTIVE);
                }
        }

        private void updateLeaseStatusIfRequired(LeasePaymentStatus leasePaymentStatus,
                        LeaseContractStatus leaseContractStatus, LeaseContract leaseContract) {
                if (LeasePaymentStatus.TERMINATED.equals(leasePaymentStatus)
                                && !LeaseContractStatus.TERMINATED.equals(leaseContractStatus)) {
                        leaseContract.setStatus(LeaseContractStatus.TERMINATED);
                        leaseContract.getVehicleNo().setDeployed(false);
                        leaseContractRepository.saveAndFlush(leaseContract);
                } else if (LeasePaymentStatus.DEFAULTED.equals(leasePaymentStatus)
                                && (!LeaseContractStatus.DEFAULTED.equals(leaseContractStatus)
                                                && !LeaseContractStatus.TERMINATED.equals(leaseContractStatus))) {
                        leaseContract.setStatus(LeaseContractStatus.DEFAULTED);
                        leaseContractRepository.saveAndFlush(leaseContract);
                } else if (LeasePaymentStatus.SHORT_CLOSE.equals(leasePaymentStatus)
                                && !LeaseContractStatus.SHORT_CLOSE.equals(leaseContractStatus)) {
                        leaseContract.setStatus(LeaseContractStatus.SHORT_CLOSE);
                        leaseContract.getVehicleNo().setDeployed(false);
                        leaseContractRepository.saveAndFlush(leaseContract);
                } else if (LeasePaymentStatus.PAID.equals(leasePaymentStatus)
                                && LeaseContractStatus.DEFAULTED.equals(leaseContractStatus)) {

                        leaseContract.setStatus(LeaseContractStatus.ACTIVE);
                        leaseContractRepository.saveAndFlush(leaseContract);
                }
        }

        public void updateOtherAmortizationEntries(AmortizationEntryUpdateDTO amortizationEntryUpdateDTO) {
                Optional<AmortizationEntry> amortizationEntry = amortizationEntryRepository
                                .findById(amortizationEntryUpdateDTO.getAmortizationEntryId());

                if (amortizationEntry.isPresent()
                                && amortizationEntryUpdateDTO.getStatus().equals(LeasePaymentStatus.PAID)) {
                        val amortizationEntryToUpdate = amortizationEntry.get();
                        List<AmortizationEntry> allEntries = amortizationEntryToUpdate.getLeaseContract()
                                        .getAmortizationEntries();
                        allEntries.stream().filter(
                                        amortizationEntry1 -> amortizationEntry1.getDate()
                                                        .isBefore(amortizationEntryToUpdate.getDate()))
                                        .filter(amortizationEntry1 -> amortizationEntry1.getLeasePaymentStatus()
                                                        .equals(LeasePaymentStatus.PENDING))
                                        .forEach(amortizationEntry1 -> amortizationEntry1
                                                        .setLeasePaymentStatus(LeasePaymentStatus.PAID));
                        allEntries.stream().filter(
                                        amortizationEntry1 -> amortizationEntry1.getDate()
                                                        .isBefore(amortizationEntryToUpdate.getDate()))
                                        .filter(amortizationEntry1 -> amortizationEntry1.getLeasePaymentStatus()
                                                        .equals(LeasePaymentStatus.NO_RENT_DAY_FUTURE))
                                        .forEach(amortizationEntry1 -> amortizationEntry1
                                                        .setLeasePaymentStatus(LeasePaymentStatus.NO_RENT_DAY));
                        amortizationEntryRepository.saveAllAndFlush(allEntries);
                        updateLeaseStatusIfRequired(amortizationEntryToUpdate.getLeasePaymentStatus(),
                                        amortizationEntryToUpdate.getLeaseContract().getStatus(),
                                        amortizationEntryToUpdate.getLeaseContract());
                }
        }

}
