package com.spinteg.lv.dto.mapper;

import com.spinteg.lv.dto.ContractTypeDTO;
import com.spinteg.lv.dto.LeaseContractDTO;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.entity.LeaseContract;
import com.spinteg.lv.entity.AmortizationEntry;
import com.spinteg.lv.dto.LeaseContractWithAmortizationDTO;
import com.spinteg.lv.dto.AmortizationEntryDto;
import lombok.val;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class LeaseContractMapper {

        public static ContractTypeDTO toContractTypeDTO(ContractType contractType) {
                return new ContractTypeDTO(
                                contractType.getContractTypeId(),
                                contractType.getTenure(),
                                contractType.getInterestRate(),
                                contractType.getInstallment(),
                                contractType.getPaymentFrequency(),
                                contractType.getDeployment(),
                                contractType.getTypeOfVehicle(),
                                contractType.getBaseRent(),
                                contractType.getMaintenanceAmount(),
                                contractType.getInsuranceAmount(),
                                contractType.getDownpayment());
        }

        public static LeaseContractDTO toLeaseContractDTO(LeaseContract leaseContract) {
                return LeaseContractDTO.builder()
                                .contractNo(leaseContract.getContractNo())
                                .customerNumber(leaseContract.getCustomerId())
                                .vehicleNo(leaseContract.getVehicleNo().getVehicleNo())
                                .customerName(leaseContract.getCustomerName())
                                .leaseContractStatus(leaseContract.getStatus())
                                .startDate(leaseContract.getStartDate())
                                .endDate(leaseContract.getEndDate())
                                .revenuePending(leaseContract.getRevenuePending())
                                .valueOfLeaseToday(leaseContract.getValueOfLeaseToday())
                                .city(leaseContract.getCity())
                                .country(leaseContract.getCountry())
                                .contractTypeDTO(toContractTypeDTO(leaseContract.getContractType()))
                                .vehicleStatus(leaseContract.getVehicleStatus())
                                .noOfTimesDeployed(leaseContract.getVehicleNo().getNoOfTimesDeployed())
                                .sumOfPvOfCf(leaseContract.getSumOfPvOfCf())
                                .build();
        }

        // Converts LeaseContract entity to LeaseContractDTO
        public static LeaseContractWithAmortizationDTO toLeaseContractWithAmortizationDTO(LeaseContract leaseContract) {
                LeaseContractWithAmortizationDTO leaseContractWithAmortizationDTO = new LeaseContractWithAmortizationDTO();

                // Mapping LeaseContract fields
                leaseContractWithAmortizationDTO.setContractNo(leaseContract.getContractNo());
                leaseContractWithAmortizationDTO.setCustomerId(leaseContract.getCustomerId());
                try{
                        leaseContractWithAmortizationDTO.setVehicleNo(leaseContract.getVehicleNo().getVehicleNo());
                }
                catch (Exception e) {
                        leaseContractWithAmortizationDTO.setVehicleNo("null");
                }
                leaseContractWithAmortizationDTO.setCustomerName(leaseContract.getCustomerName());
                leaseContractWithAmortizationDTO.setCustomerNumber(leaseContract.getCustomerNumber());
                leaseContractWithAmortizationDTO.setStartDate(leaseContract.getStartDate());
                leaseContractWithAmortizationDTO.setEndDate(leaseContract.getEndDate());
                leaseContractWithAmortizationDTO.setStatus(leaseContract.getStatus());
                leaseContractWithAmortizationDTO.setRevenuePending(leaseContract.getRevenuePending());
                leaseContractWithAmortizationDTO.setValueOfLeaseToday(leaseContract.getValueOfLeaseToday());
                leaseContractWithAmortizationDTO.setContractType(leaseContract.getContractType());
                leaseContractWithAmortizationDTO.setVehicleStatus(leaseContract.getVehicleStatus());
                leaseContractWithAmortizationDTO.setCity(leaseContract.getCity());
                leaseContractWithAmortizationDTO.setCountry(leaseContract.getCountry());
                // Mapping LeasePaymentEntries
                List<AmortizationEntryDto> amortizationEntryDtos = leaseContract.getAmortizationEntries()
                                .stream()
                                .map(LeaseContractMapper::toLeaseContractWithAmortizationDTO)
                                .sorted(Comparator.comparing(AmortizationEntryDto::getPeriod))
                                .collect(Collectors.toList());

                leaseContractWithAmortizationDTO.setLeasePaymentEntries(amortizationEntryDtos);
                leaseContractWithAmortizationDTO.setSumOfPvOfCf(BigDecimal.valueOf(
                                amortizationEntryDtos.stream().map(AmortizationEntryDto::getPvOfCf).reduce(Double::sum)
                                                .get()));
                leaseContractWithAmortizationDTO.setTotalInterest(BigDecimal.valueOf(amortizationEntryDtos.stream()
                                .map(AmortizationEntryDto::getInterestComponent).reduce(Double::sum).get()));
                leaseContractWithAmortizationDTO.setTotalLeasePayments(BigDecimal.valueOf(
                                amortizationEntryDtos.stream().map(AmortizationEntryDto::getInstallment)
                                                .reduce(Double::sum).get()));
                return leaseContractWithAmortizationDTO;
        }

        // Converts LeasePaymentEntry entity to LeasePaymentEntryDTO
        public static AmortizationEntryDto toLeaseContractWithAmortizationDTO(AmortizationEntry amortizationEntry) {
                AmortizationEntryDto amortizationEntryDto = new AmortizationEntryDto();
                val interestComponent = amortizationEntry.getInterestComponent();
                val principalComponent = amortizationEntry.getPrincipalComponent();
                // Mapping LeasePaymentEntry fields
                amortizationEntryDto.setLeasePaymentEntryId(amortizationEntry.getAmortizationEntryId());
                amortizationEntryDto.setDate(amortizationEntry.getDate());
                amortizationEntryDto.setPeriod(amortizationEntry.getPeriod());
                amortizationEntryDto.setBaseRent(amortizationEntry.getBaseRent());
                amortizationEntryDto.setInstallment(amortizationEntry.getInstallment());
                amortizationEntryDto.setBaseRent(amortizationEntry.getBaseRent());
                amortizationEntryDto.setInterestComponent(interestComponent);
                amortizationEntryDto.setPrincipalComponent(principalComponent);
                amortizationEntryDto.setRemainingAmount(amortizationEntry.getRemainingAmount());
                amortizationEntryDto.setLeasePaymentStatus(amortizationEntry.getLeasePaymentStatus());
                amortizationEntryDto.setCumulativeInstallment(amortizationEntry.getCumulativeInstallment());
                amortizationEntryDto.setCumulativePrincipal(amortizationEntry.getCumulativePrincipal());
                amortizationEntryDto.setCumulativeInterest(amortizationEntry.getCumulativeInterest());
                amortizationEntryDto.setClosingBalance(amortizationEntry.getClosingBalance());
                amortizationEntryDto
                                .setPercentageOfInterest(interestComponent / (interestComponent + principalComponent));
                amortizationEntryDto.setPercentageOfPrincipal(
                                principalComponent / (interestComponent + principalComponent));
                amortizationEntryDto.setPvOfCf(amortizationEntry.getPvOfCf());
                amortizationEntryDto.setTransactionDate(amortizationEntry.getTransactionDate());
                return amortizationEntryDto;
        }
}
