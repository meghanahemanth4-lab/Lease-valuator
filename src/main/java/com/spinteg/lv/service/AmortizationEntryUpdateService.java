package com.spinteg.lv.service;

import com.amazonaws.services.ec2.model.WeekDay;
import com.spinteg.lv.dto.AmortizationEntryUpdateDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;
import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataValidation;
import com.spinteg.lv.repository.AmortizationEntryRepository;
import com.spinteg.lv.repository.LeaseContractRepository;
import lombok.AllArgsConstructor;
import lombok.val;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AmortizationEntryUpdateService {
        private final HolidayService holidayService;
        private final AmortizationEntryRepository amortizationEntryRepository;
        private final LeaseContractRepository leaseContractRepository;

        public boolean updateAmortizationEntry(UUID amortizationEntryId, LeasePaymentStatus status,
                        LocalDate entryDate) {
                Optional<AmortizationEntry> amortizationEntry = amortizationEntryRepository
                                .findById(amortizationEntryId);
                if (amortizationEntry.isPresent()) {
                        val amortizationEntryToUpdate = amortizationEntry.get();
                        val statusBeforeUpdate = amortizationEntryToUpdate.getLeasePaymentStatus();
                        amortizationEntryToUpdate.setLeasePaymentStatus(status);
                        amortizationEntryToUpdate.setTransactionDate(entryDate);
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

        public ResponseEntity<?> updateAmortizationEntryToNoRentDay(LocalDate date) {

                DataValidation.isValidDate(date.toString(), "Date");

                if (date.isBefore(LocalDate.now()))
                        throw new CustomDataMissingException("It cannot be applied to no-rent day");

                if (checkIfSunday(date)) // Check if the date is a Sunday
                {
                        throw new CustomDataMissingException("It cannot be applied to no-rent day on Sunday");
                }
                List<LeaseContract> leaseContracts = leaseContractRepository.findByStatus(LeaseContractStatus.ACTIVE);

                for (LeaseContract contract : leaseContracts) {
                        changeToNoRentDay(date, contract);
                }
                return ResponseEntity.ok().body("No rent day is updated successfully");
        }

        public void changeToNoRentDay(LocalDate date, LeaseContract contract) {

                List<AmortizationEntry> amortizationEntries = contract.getAmortizationEntries();
                List<String> holidaysList = holidayService.getHolidaysDatesForCountryAndYear(contract.getCountry());
                if (amortizationEntries.isEmpty()) {
                        return;
                }

                AmortizationEntry entryForDate = amortizationEntries.stream()
                                .filter(amortizationEntry -> amortizationEntry.getDate().isEqual(date))
                                .findFirst()
                                .orElse(null);

                if (entryForDate == null) {
                        return;
                }

                LeasePaymentStatus contractStatus = entryForDate.getLeasePaymentStatus();
                if (contractStatus == LeasePaymentStatus.NO_RENT_DAY_FUTURE
                                || contractStatus == LeasePaymentStatus.NO_RENT_DAY) {
                        return;
                }

                handleNoRentDay(amortizationEntries, date, contract, holidaysList);
        }

        @Transactional
        private void handleNoRentDay(List<AmortizationEntry> entries, LocalDate targetDate,
                        LeaseContract contract, List<String> holidays) {

                AmortizationEntry targetEntry = null;
                boolean created = false;
                AmortizationEntry previousEntry = null;

                entries.sort(Comparator.comparing(AmortizationEntry::getPeriod));

                for (AmortizationEntry entry : new ArrayList<>(entries)) {
                        previousEntry = entry;
                        if (!created && entry.getDate().isEqual(targetDate)) {

                                targetEntry = entry;

                                AmortizationEntry noRentEntryFirst = noRentEntryCalculationForFirstNoRentDay(entry,
                                                contract,
                                                targetDate,
                                                entry.getPeriod(), previousEntry);

                                // contract.getAmortizationEntries().remove(entry);
                                // amortizationEntryRepository.deleteById(entry.getAmortizationEntryId());
                                contract.getAmortizationEntries().remove(entry); // remove from parent
                                entry.setLeaseContract(null); // optional but helps clarify disassociation
                                amortizationEntryRepository.delete(entry);
                                amortizationEntryRepository.flush();

                                amortizationEntryRepository.save(noRentEntryFirst);
                                created = true;
                                continue;
                        }

                        else if (created && targetEntry != null) {
                                if (entry.getDate().isBefore(targetDate)) {
                                        continue;
                                }
                                adjustEntryValues(entry, targetEntry);

                                if (entry.getDate().isEqual(contract.getEndDate())) {

                                        LocalDate checkDate = contract.getEndDate().plusDays(1);
                                        AmortizationEntry lastEntry = entry;

                                        while (checkIfWeekendOrHoliday(checkDate, holidays)) {
                                                AmortizationEntry noRentEntry = noRentEntryCalculation(lastEntry,
                                                                contract,
                                                                checkDate, lastEntry.getPeriod() + 1);
                                                // contract.getAmortizationEntries().add(noRentEntry);
                                                lastEntry = amortizationEntryRepository.save(noRentEntry);
                                                checkDate = checkDate.plusDays(1);
                                        }

                                        AmortizationEntry finalEntry = penndingEntryCalculation(contract, targetEntry,
                                                        lastEntry, lastEntry.getPeriod() + 1);
                                        contract.setEndDate(finalEntry.getDate());
                                        amortizationEntryRepository.save(finalEntry);
                                        leaseContractRepository.save(contract);
                                        leaseContractRepository.flush();
                                }
                        }
                }

                amortizationEntryRepository.flush();

        }

        private AmortizationEntry penndingEntryCalculation(LeaseContract contract, AmortizationEntry originalEntry,
                        AmortizationEntry lastEntry, long period) {
                AmortizationEntry amortizationEntry = AmortizationEntry.builder()
                                .leaseContract(contract)
                                .date(lastEntry.getDate().plusDays(1))
                                .period(period)
                                .installment(originalEntry.getInstallment())
                                .insurance(originalEntry.getInsurance())
                                .maintenance(originalEntry.getMaintenance())

                                .principalComponent(originalEntry.getPrincipalComponent())
                                .cumulativePrincipal(lastEntry.getCumulativePrincipal()
                                                + originalEntry.getPrincipalComponent())

                                .baseRent(originalEntry.getBaseRent())
                                .cumulativeInstallment(
                                                lastEntry.getCumulativeInstallment() + originalEntry.getBaseRent())

                                .interestComponent(lastEntry.getInterestComponent())
                                .cumulativeInterest(
                                                lastEntry.getCumulativeInterest()
                                                                + lastEntry.getInterestComponent())
                                // .closingBalance(lastEntry.getClosingBalance())

                                .leasePaymentStatus(LeasePaymentStatus.PENDING)
                                .remainingAmount(lastEntry.getRemainingAmount())
                                .pvOfCf(originalEntry.getPvOfCf())
                                .build();
                return amortizationEntry;
        }

        private AmortizationEntry noRentEntryCalculationForFirstNoRentDay(AmortizationEntry originalEntry,
                        LeaseContract contract,
                        LocalDate targetDate, Long period, AmortizationEntry previousEntry) {

                double cumulativePrincipal = originalEntry.getCumulativePrincipal()
                                - originalEntry.getPrincipalComponent();

                cumulativePrincipal = adjustNearZero(cumulativePrincipal);

                AmortizationEntry noRentEntry = AmortizationEntry.builder()
                                .leaseContract(contract)
                                .date(targetDate)
                                .period(period)
                                .installment(originalEntry.getInstallment())
                                .baseRent(0.0)
                                .insurance(originalEntry.getInsurance())
                                .maintenance(originalEntry.getMaintenance())
                                .interestComponent(originalEntry.getInterestComponent())
                                .closingBalance(originalEntry.getClosingBalance())
                                .cumulativeInstallment(
                                                originalEntry.getCumulativeInstallment())
                                .cumulativeInterest(originalEntry.getCumulativeInterest())
                                .leasePaymentStatus(LeasePaymentStatus.NO_RENT_DAY)
                                .cumulativePrincipal(cumulativePrincipal)
                                .leaseContract(contract)
                                .principalComponent(0.0)
                                .remainingAmount(originalEntry.getRemainingAmount())
                                .pvOfCf(0.0)
                                .build();
                return noRentEntry;
        }

        private AmortizationEntry noRentEntryCalculation(AmortizationEntry originalEntry, LeaseContract contract,
                        LocalDate targetDate, Long period) {
                // Create placeholder No-Rent Day entry
                AmortizationEntry noRentEntry = AmortizationEntry.builder()
                                .leaseContract(contract)
                                .date(targetDate)
                                .period(period)
                                .installment(originalEntry.getInstallment())
                                .baseRent(0.0)
                                .insurance(originalEntry.getInsurance())
                                .maintenance(originalEntry.getMaintenance())
                                .interestComponent(originalEntry.getInterestComponent())
                                .cumulativePrincipal(originalEntry.getCumulativePrincipal())
                                .cumulativeInstallment(
                                                originalEntry.getCumulativeInstallment() + originalEntry.getBaseRent())
                                .cumulativeInterest(originalEntry.getCumulativeInterest()
                                                + originalEntry.getInterestComponent())
                                .closingBalance(originalEntry.getClosingBalance())
                                .principalComponent(0.0)
                                .leasePaymentStatus(LeasePaymentStatus.NO_RENT_DAY_FUTURE)
                                .remainingAmount(originalEntry.getRemainingAmount())
                                .pvOfCf(0.0)
                                .build();
                return noRentEntry;
        }

        // private AmortizationEntry
        // createExtraAmortizationEntry(List<AmortizationEntry> entries,
        // LocalDate baseDate,
        // LeaseContract contract,
        // List<String> holidays,
        // AmortizationEntry baseEntry,
        // AmortizationEntry lastEntry) {

        // LocalDate newDate = entries.stream()
        // .map(AmortizationEntry::getDate)
        // .max(LocalDate::compareTo)
        // .orElse(baseDate)
        // .plusDays(1);

        // // Skip weekends/holidays
        // while (checkIfWeekendOrHoliday(newDate, holidays) ||
        // newDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
        // newDate = newDate.plusDays(1);
        // }

        // newDate = newDate.plusDays(1); // Move to the next date after holiday/weekend

        // return AmortizationEntry.builder()
        // .leaseContract(contract)
        // .date(newDate)
        // .period(ChronoUnit.DAYS.between(newDate, baseDate) > 0
        // ? ChronoUnit.DAYS.between(newDate, baseDate)
        // : -(ChronoUnit.DAYS.between(newDate, baseDate)))
        // .installment(Double.valueOf(contract.getContractType().getInstallment()))
        // .baseRent(Double.valueOf(contract.getContractType().getBaseRent()))
        // .insurance(Double.valueOf(contract.getContractType().getInsuranceAmount()))
        // .maintenance(Double.valueOf(contract.getContractType().getMaintenanceAmount()))
        // .interestComponent(baseEntry.getInterestComponent())
        // .principalComponent(baseEntry.getPrincipalComponent())
        // .cumulativePrincipal(
        // lastEntry.getCumulativePrincipal() + baseEntry.getCumulativePrincipal())
        // .cumulativeInstallment(lastEntry.getCumulativeInstallment()
        // + baseEntry.getCumulativeInstallment())
        // .cumulativeInterest(
        // lastEntry.getCumulativeInterest() + baseEntry.getCumulativeInterest())
        // .closingBalance(lastEntry.getClosingBalance() +
        // baseEntry.getClosingBalance())
        // .remainingAmount(lastEntry.getRemainingAmount() +
        // baseEntry.getRemainingAmount())
        // .pvOfCf(baseEntry.getPvOfCf())
        // .leasePaymentStatus(LeasePaymentStatus.PENDING)
        // .build();
        // }

        private boolean checkIfWeekendOrHoliday(LocalDate currentDate, List<String> holidaysList) {
                if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY
                                || DailyLeaseScheduleCalculator.isHoliday(currentDate, holidaysList)) {
                        return true;
                }
                return false;
        }

        private boolean checkIfSunday(LocalDate currentDate) {
                if (currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        return true;
                }
                return false;
        }

        private void adjustEntryValues(AmortizationEntry entry, AmortizationEntry targetEntry) {
                double newCumulativePrincipal = entry.getCumulativePrincipal() - targetEntry.getPrincipalComponent();
                newCumulativePrincipal = adjustNearZero(newCumulativePrincipal);
                entry.setCumulativePrincipal(newCumulativePrincipal);
        }

        private double adjustNearZero(double val) {
                BigInteger cumulativePrincipalValue = BigInteger.valueOf((long) val);

                if (cumulativePrincipalValue.compareTo(BigInteger.ZERO) <= 0) {
                        val = 0.0d;
                }
                return val;
        }

}
