package com.spinteg.lv.service;

import com.spinteg.lv.exception.CustomDataMissingException;
import com.spinteg.lv.exception.DataNotFoundException;
import com.spinteg.lv.exception.DataValidation;
import com.spinteg.lv.exception.ResponseHandler;
import com.spinteg.lv.dto.CreateContractTypeDTO;
import com.spinteg.lv.dto.UpdateContractTypeDTO;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.entity.enums.Deployment;
import com.spinteg.lv.entity.enums.Frequency;
import com.spinteg.lv.entity.enums.VehicleStatus;
import com.spinteg.lv.entity.enums.VehicleType;
import com.spinteg.lv.repository.ContractTypeRepository;
import com.spinteg.lv.repository.InterestRateRepository;
import lombok.AllArgsConstructor;
import lombok.val;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContractTypeService {

    private final ContractTypeRepository contractTypeRepository;
    private final InterestRateRepository interestRateRepository;

    public ContractType getContractTypeById(String contractTypeId) {
        return contractTypeRepository.findById(contractTypeId)
                .orElseThrow(
                        () -> new DataNotFoundException("Contract Type not found with ID: " + contractTypeId));
    }

    public List<String> getAllContractTypeIds() {
        return contractTypeRepository.findAllContractTypeIds();
    }

    public ResponseEntity<?> saveContractType(CreateContractTypeDTO createContractTypeDTO) {
        ContractType contractType = new ContractType();

        Optional<ContractType> optional = contractTypeRepository.findById(createContractTypeDTO.getContractName());
        if (optional.isPresent()) {
            throw new CustomDataMissingException(
                    "Contract Type is found with ID: " + createContractTypeDTO.getContractName());
        }

        try {
            Frequency.valueOf(createContractTypeDTO.getPaymentFrequency());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomDataMissingException(
                    "Invalid Payment Frequency: " + createContractTypeDTO.getPaymentFrequency());
        }

        try {
            Deployment.valueOf(createContractTypeDTO.getDeployment());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomDataMissingException(
                    "Invalid Deployment Type: " + createContractTypeDTO.getDeployment());
        }
        contractType.setBaseRent(null);
        contractType.setInsuranceAmount(null);
        contractType.setMaintenanceAmount(null);
        contractType.setSwap(null);
        contractType.setVehicleStatus(VehicleStatus.NEW);
        contractType.setContractTypeId(createContractTypeDTO.getContractName());
        contractType.setContractCode(createContractTypeDTO.getContractCode());
        contractType.setTenure(Integer.parseInt(
                DataValidation.isNotZero(
                        DataValidation.isInteger(createContractTypeDTO.getTenure(), "Tenure"), "Tenure")));
        contractType.setInterestRate(interestRateRepository.findById(1L)
                .orElseThrow(() -> new CustomDataMissingException("Interest rate not found")).getRate());

        contractType.setPaymentFrequency(Frequency.valueOf(createContractTypeDTO.getPaymentFrequency()));
        contractType.setTypeOfVehicle(fromString(createContractTypeDTO.getTypeOfVehicle()));
        contractType.setDeployment(Deployment.valueOf(createContractTypeDTO.getDeployment()));

        contractType.setDownpayment(
                Float.parseFloat(DataValidation.isNumeric(createContractTypeDTO.getDownPayment(), "Downpayment")));
        contractType.setInstallment(Float.parseFloat(
                DataValidation.isNotZero(
                        DataValidation.isNumeric(createContractTypeDTO.getInstallment(), "Installment"),
                        "Installment")));
        contractTypeRepository.save(contractType);
        return ResponseHandler.handleResponseEntity("ContractType Created", contractType, null);
    }

    public List<ContractType> getALlContractTypes() {
        val allContractTypes = contractTypeRepository.findAll();
        if (allContractTypes.isEmpty())
            return List.of();
        return allContractTypes;
    }

    public void updateInterestRateForAllContracts(float newRate) {
        List<ContractType> allContracts = contractTypeRepository.findAll();
        for (ContractType contract : allContracts) {
            contract.setInterestRate(newRate);
            contractTypeRepository.save(contract);
        }
    }

    public static VehicleType fromString(String value) {
        try {
            return VehicleType.valueOf(value.replace(' ', '_').replace("-", "_").toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomDataMissingException("Invalid vehicle type: " + value);
        }
    }

    public List<ContractType> getContractTypesWithNullBaseRent() {
        return contractTypeRepository.findByBaseRentIsNull();
    }

    public ResponseEntity<?> updateContractTypeFields(String contractTypeId, UpdateContractTypeDTO objContractType) {
        ContractType contractType = contractTypeRepository.findById(contractTypeId)
                .orElseThrow(() -> new DataNotFoundException("ContractType not found"));

        // Checking validation for the fields
        upadteContractTypeFieldsValidation(contractType, objContractType);
        return ResponseHandler.handleResponseEntity("ContractType Updated", contractTypeRepository.save(contractType),
                null);
    }

    public void upadteContractTypeFieldsValidation(ContractType contractType, UpdateContractTypeDTO objContractType) {

        if (objContractType.getBaseRent() == null || objContractType.getBaseRent().trim() == "")
            throw new CustomDataMissingException("Base Rent cannot be null or empty");

        if (objContractType.getInsuranceAmount() == null || objContractType.getInsuranceAmount().trim() == "")
            throw new CustomDataMissingException("Insurence Amount cannot be null or empty");

        if (objContractType.getMaintenanceAmount() == null || objContractType.getMaintenanceAmount().trim() == "")
            throw new CustomDataMissingException("Maintenance Amount cannot be null or empty");

        // if (objContractType.getSwapAmount() == null ||
        // objContractType.getSwapAmount().trim() == "") {
        // throw new CustomDataMissingException("Swap Amount cannot be null or empty");
        // }
        // if (Integer.valueOf(objContractType.getBaseRent().trim()) <= 0)
        // throw new CustomDataMissingException("Base Rent should be greater than 0");

        contractType.setBaseRent(
                Integer.valueOf(
                        DataValidation.isInteger(
                                DataValidation.isNotZero(
                                        objContractType.getBaseRent(), "Base Rent"),
                                "Base Rent")));

        contractType.setInsuranceAmount(
                Float.valueOf(
                        DataValidation.isInteger(objContractType.getMaintenanceAmount(), "Maintenance Amount")));

        contractType.setMaintenanceAmount(
                Float.valueOf(
                        DataValidation.isInteger(objContractType.getInsuranceAmount(), "Insurance Amount")));

        contractType.setSwap(
                Float.valueOf(
                        DataValidation.isInteger(objContractType.getSwapAmount(), "Swap Amount")));

        if (contractType.getInstallment() != (contractType.getInsuranceAmount() + contractType.getMaintenanceAmount()
                + contractType.getBaseRent() + contractType.getSwap()))
            throw new CustomDataMissingException(
                    "Installment Amount is not equal to sum of Base Rent, Insurance Amount, Maintenance Amount and Swap Amount");

    }

}
