package com.spinteg.lv.dto.mapper;

import com.spinteg.lv.dto.AmortizationEntryDto;
import com.spinteg.lv.dto.TerminatedLeaseContractDTO;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.enums.LeaseContractStatus;
import com.spinteg.lv.entity.enums.LeasePaymentStatus;

import lombok.val;

import static com.spinteg.lv.dto.mapper.MapperUtils.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class TerminatedLeaseContractMapper {

        public static TerminatedLeaseContractDTO toTerminatedLeaseContractDTO(LeaseContract leaseContract) {
                val leaseContractWithAmortizationDTO = LeaseContractMapper
                                .toLeaseContractWithAmortizationDTO(leaseContract);
                val amortizationSchedules = leaseContractWithAmortizationDTO.getLeasePaymentEntries();
                val revenueToRecognise = leaseContractWithAmortizationDTO.getSumOfPvOfCf();
                val interestToRecognise = sumByField(getSchedulesByStatus(amortizationSchedules, activeStatuses),
                                AmortizationEntryDto::getInterestComponent);
                val interestReversal = sumByField(getSchedulesByStatus(amortizationSchedules, defaultAndNoRentDay),
                                AmortizationEntryDto::getInterestComponent);
                val totalMoneyReceived = sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                AmortizationEntryDto::getBaseRent);

                LocalDate dateOfTermination = deriveTerminationDate(amortizationSchedules);

                return TerminatedLeaseContractDTO.builder()
                                .leaseContractDTO(LeaseContractMapper.toLeaseContractDTO(leaseContract))
                                .activePeriods(getSchedulesByStatus(amortizationSchedules, activeStatuses).size())
                                .defaultPeriods(getSchedulesByStatus(amortizationSchedules, defaultStatuses).size())
                                .paidPeriods(getSchedulesByStatus(amortizationSchedules, paidStatuses).size())
                                .remainingPeriods(leaseContract.getContractType().getTenure()
                                                - getSchedulesByStatus(amortizationSchedules, activeStatuses).size())
                                .totalMoneyReceived(totalMoneyReceived)
                                .interestReversal(interestReversal)
                                .principalReceived(sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                                AmortizationEntryDto::getPrincipalComponent))
                                .interestRecognised(interestToRecognise)
                                .revenueRecognised(revenueToRecognise)
                                .grossDebtor(revenueToRecognise.add(interestToRecognise).subtract(interestReversal)
                                                .subtract(totalMoneyReceived))

                                // New Fields
                                .startDate(leaseContract.getStartDate())
                                .numberOfNoRentDay(
                                                getSchedulesByStatus(amortizationSchedules,
                                                                List.of(LeasePaymentStatus.NO_RENT_DAY)).size())
                                .dateOfTermination(dateOfTermination)
                                .noOfTimesDeployed(leaseContract.getNoOfTimesDeployed())

                                .build();
        }

        private static LocalDate deriveTerminationDate(List<AmortizationEntryDto> amortizationSchedules) {
                return amortizationSchedules.stream()
                                .filter(schedule -> schedule.getLeasePaymentStatus() == LeasePaymentStatus.TERMINATED)
                                .map(AmortizationEntryDto::getDate)
                                .min(Comparator.naturalOrder())
                                .orElse(null);
        }

}
