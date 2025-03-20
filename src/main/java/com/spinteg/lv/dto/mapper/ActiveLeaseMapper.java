package com.spinteg.lv.dto.mapper;

import com.spinteg.lv.dto.ActiveLeaseDTO;
import com.spinteg.lv.dto.AmortizationEntryDto;
import com.spinteg.lv.dto.LeaseContractWithAmortizationDTO;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.entity.LeaseContract;
import lombok.val;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.spinteg.lv.dto.mapper.MapperUtils.*;

public class ActiveLeaseMapper {

        public static ActiveLeaseDTO toActiveLeaseDTO(LeaseContract leaseContract) {
                ActiveLeaseDTO dto = new ActiveLeaseDTO();
                dto.setContractType(leaseContract.getContractType().getContractTypeId());
                dto.setCountry(leaseContract.getCountry());
                dto.setCity(leaseContract.getCity());
                dto.setContractNo(leaseContract.getContractNo());
                dto.setCustomerId(leaseContract.getCustomerId());
                dto.setCustomerName(leaseContract.getCustomerName());
                dto.setVehicleType(leaseContract.getContractType().getTypeOfVehicle());
                dto.setVehicleNumber(leaseContract.getVehicleNo());
                dto.setStartDate(leaseContract.getStartDate());
                dto.setEndDate(leaseContract.getEndDate());
                dto.setDeployment(leaseContract.getContractType().getDeployment());
                dto.setVehicleStatus(leaseContract.getVehicleStatus());
                dto.setTenure(leaseContract.getContractType().getTenure());
                dto.setInstallment(Double.valueOf(leaseContract.getContractType().getInstallment()));
                dto.setNoOfTimesRedeployed(leaseContract.getNoOfTimesRedeployed());

                val leaseContractWithAmortizationDTO = LeaseContractMapper
                                .toLeaseContractWithAmortizationDTO(leaseContract);
                val amortizationSchedules = leaseContractWithAmortizationDTO.getLeasePaymentEntries();

                // Calculate activePeriods
                int activePeriods = getSchedulesByStatus(amortizationSchedules, activeStatuses).size();
                dto.setActivePeriods(activePeriods);

                // Calculate defaultPeriods
                int defaultPeriods = getSchedulesByStatus(amortizationSchedules, defaultStatuses).size();
                dto.setDefaultPeriods(defaultPeriods);

                // Calculate revenueReceived
                BigDecimal revenueReceived = sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                AmortizationEntryDto::getInstallment);
                dto.setRevenueReceived(revenueReceived.doubleValue());

                // Calculate interestReceived
                BigDecimal interestReceived = sumByField(getSchedulesByStatus(amortizationSchedules, paidStatuses),
                                AmortizationEntryDto::getInterestComponent);
                dto.setInterestReceived(interestReceived.doubleValue());

                // Calculate outstandingBalance
                LocalDate today = LocalDate.now();
                Double outstandingBalance = amortizationSchedules.stream()
                                .filter(entry -> entry.getDate().equals(today))
                                .map(AmortizationEntryDto::getRemainingAmount)
                                .findFirst()
                                .orElse(0.0);
                dto.setOutstandingBalance(outstandingBalance);

                // Calculate reversalIfTerminated (same as grossDebtor in
                // TerminatedLeaseContractMapper)
                BigDecimal interestReversal = sumByField(getSchedulesByStatus(amortizationSchedules, defaultStatuses),
                                AmortizationEntryDto::getInterestComponent);
                BigDecimal reversalIfTerminated = leaseContractWithAmortizationDTO.getSumOfPvOfCf()
                                .add(interestReceived)
                                .subtract(interestReversal)
                                .subtract(revenueReceived);
                dto.setReversalIfTerminated(reversalIfTerminated.doubleValue());
                dto.setLastSevenDaysData(getLastSevenDaysDataFunc(leaseContract));
                dto.setLastSevenDaysDates(getLastSevenDaysDatesFunc());

                return dto;
        }

        public static List<Object> getLastSevenDaysDataFunc(LeaseContract contract) {

                LocalDate today = LocalDate.now().minusDays(2);
                LocalDate sevenDaysAgo = today.minusDays(0);

                List<Object> result = new ArrayList<>();

                // Convert LeaseContract to DTO
                var leaseContractWithAmortizationDTO = LeaseContractMapper.toLeaseContractWithAmortizationDTO(contract);

                // Ensure amortizationSchedules is not null
                List<AmortizationEntryDto> amortizationSchedules = Optional.ofNullable(
                                leaseContractWithAmortizationDTO.getLeasePaymentEntries()).orElse(new ArrayList<>());

                if (amortizationSchedules.isEmpty())
                        return result;
                for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
                        final LocalDate searchDate = date;

                        Optional<AmortizationEntryDto> entry = amortizationSchedules.stream()
                                        .filter(e -> e.getDate() != null && e.getDate().equals(searchDate))
                                        .findFirst();

                        String getInstallmentValue = "-";
                        if (entry.isPresent()) {
                                AmortizationEntryDto entryDto = entry.get();
                                if ("PAID".equals(entryDto.getLeasePaymentStatus().toString())) {

                                        getInstallmentValue = Optional.ofNullable(entryDto.getInstallment())
                                                        .map(Object::toString)
                                                        .orElse("-");
                                } else {
                                        getInstallmentValue = entryDto.getLeasePaymentStatus().toString();
                                }
                        }
                        result.add(new AmortizationData(searchDate, getInstallmentValue));
                        // result.add(new AmortizationData(searchDate,
                        // entry.map(AmortizationEntryDto::getInstallment).orElse(null)));
                }

                return result;
        }

        public static List<String> getLastSevenDaysDatesFunc() {
                LocalDate today = LocalDate.now().minusDays(2);
                LocalDate sevenDaysAgo = today.minusDays(0);

                List<String> lStrings = new ArrayList<>();

                for (LocalDate date = sevenDaysAgo; !date.isAfter(today); date = date.plusDays(1)) {
                        lStrings.add(convertDate(date));
                }

                return lStrings;
        }

        public static String convertDate(LocalDate inputDate) {
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
                return inputDate.format(outputFormatter);
        }

}
