package com.spinteg.lv.dto.mapper;

import com.spinteg.lv.dto.AmortizationEntryDto;
import com.spinteg.lv.dto.DefaultedLeaseContractDTO;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;

import lombok.val;

import static com.spinteg.lv.dto.mapper.MapperUtils.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class DefaultedLeaseContractMapper {

        public static DefaultedLeaseContractDTO toDefaultedLeaseContractDTO(LeaseContract leaseContract) {
                val leaseContractWithAmortizationDTO = LeaseContractMapper
                                .toLeaseContractWithAmortizationDTO(leaseContract);
                val amortizationSchedules = leaseContractWithAmortizationDTO.getLeasePaymentEntries();
                val totalDefault = sumByField(getSchedulesByStatus(amortizationSchedules, defaultStatuses),
                                AmortizationEntryDto::getInstallment);
                val totalPaymentReceived = sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                AmortizationEntryDto::getInstallment);

                val revenueToRecognise = leaseContractWithAmortizationDTO.getSumOfPvOfCf();
                val interestToRecognise = sumByField(getSchedulesByStatus(amortizationSchedules, activeStatuses),
                                AmortizationEntryDto::getInterestComponent);
                val interestReversal = sumByField(getSchedulesByStatus(amortizationSchedules, defaultAndNoRentDay),
                                AmortizationEntryDto::getInterestComponent);
                val totalMoneyReceived = sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                AmortizationEntryDto::getInstallment);

                LocalDate lastDefaultedDate = deriveLastDefaultedDate(amortizationSchedules);

                return DefaultedLeaseContractDTO.builder()
                                .leaseContractDTO(LeaseContractMapper.toLeaseContractDTO(leaseContract))
                                .activePeriods(getSchedulesByStatus(amortizationSchedules, activeStatuses).size())
                                .defaultPeriods(getSchedulesByStatus(amortizationSchedules, defaultStatuses).size())
                                .paidPeriods(getSchedulesByStatus(amortizationSchedules, paidStatuses).size())
                                .remainingPeriods(leaseContract.getContractType().getTenure()
                                                - getSchedulesByStatus(amortizationSchedules, activeStatuses).size())
                                .principalDefault(
                                                sumByField(getSchedulesByStatus(amortizationSchedules, defaultStatuses),
                                                                AmortizationEntryDto::getPrincipalComponent))
                                .interestDefault(
                                                sumByField(getSchedulesByStatus(amortizationSchedules, defaultStatuses),
                                                                AmortizationEntryDto::getInterestComponent))
                                .totalDefault(totalDefault)
                                .totalPaymentReceived(totalPaymentReceived)
                                .totalPrincipalReceived(
                                                sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                                                AmortizationEntryDto::getPrincipalComponent))
                                .totalInterestReceived(
                                                sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                                                AmortizationEntryDto::getInterestComponent))
                                .expectedTotalPayment(totalPaymentReceived.add(totalDefault))

                                // New Field
                                .startDate(leaseContract.getStartDate())
                                .numberOfNoRentDay(getSchedulesByStatus(amortizationSchedules,
                                                List.of(LeasePaymentStatus.NO_RENT_DAY)).size())
                                .lastDefaultedDate(lastDefaultedDate)
                                .noOfTimesDeployed(leaseContract.getNoOfTimesDeployed())
                                .grossDebtor(revenueToRecognise.add(interestToRecognise).subtract(interestReversal)
                                                .subtract(totalMoneyReceived))
                                .build();

        }

        private static LocalDate deriveLastDefaultedDate(List<AmortizationEntryDto> amortizationSchedules) {
                return amortizationSchedules.stream()
                                .filter(schedule -> schedule.getLeasePaymentStatus() == LeasePaymentStatus.DEFAULTED)
                                .map(AmortizationEntryDto::getDate)
                                .max(Comparator.naturalOrder())
                                .orElse(null);
        }

}
