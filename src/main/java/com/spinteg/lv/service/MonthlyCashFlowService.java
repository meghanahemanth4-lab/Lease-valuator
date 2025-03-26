package com.spinteg.lv.service;
import com.spinteg.lv.data.MonthlyCashFlow;
import com.spinteg.lv.data.MonthlyValues;
import com.spinteg.lv.entity.ContractType;
import com.spinteg.lv.repository.ContractTypeRepository;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class MonthlyCashFlowService {
    public MonthlyCashFlow generateAmortizationSchedule(int tenure, int baseRent, double interestRate){
        MonthlyCashFlow monthlyCashFlow = new MonthlyCashFlow();
        monthlyCashFlow.setTenure(tenure);
        monthlyCashFlow.setBaseRent(baseRent);
        monthlyCashFlow.setInterestRate(interestRate/1200);
        setDefaults(monthlyCashFlow);
        setCalculatedValues(monthlyCashFlow);
        monthlyCashFlow.setMonthlyValuesList(generateMonthlyPvOfCf(monthlyCashFlow));
        monthlyCashFlow.setSumOfPvOfCf(monthlyCashFlow.getMonthlyValuesList().stream().map(MonthlyValues::getPvOfCf).reduce((a,b)-> a+b).get());
        generatePvOfCfRelatedValues(monthlyCashFlow, monthlyCashFlow.getMonthlyValuesList());
        return monthlyCashFlow;
    }
    private void generatePvOfCfRelatedValues(MonthlyCashFlow monthlyCashFlow, List<MonthlyValues> monthlyValuesList) {
        for(int i=0;i<monthlyValuesList.size();i++){
            MonthlyValues monthi = monthlyValuesList.get(i);
            if(i==0) {
                monthi.setOutstandingAmount(monthlyCashFlow.getSumOfPvOfCf());
                monthi.setInterestAmount(monthi.getOutstandingAmount() * monthlyCashFlow.getInterestRate());
                monthi.setCumulativeInterestPaid(monthi.getInterestAmount());
            }
            else{
                monthi.setOutstandingAmount(monthlyValuesList.get(i-1).getClosingBalance());
                monthi.setInterestAmount(monthi.getOutstandingAmount() * monthlyCashFlow.getInterestRate());
                monthi.setCumulativeInterestPaid(monthlyValuesList.get(i-1).getCumulativeInterestPaid()+monthi.getInterestAmount());
            }
            monthi.setClosingBalance(monthi.getOutstandingAmount()+monthi.getInterestAmount()-monthi.getMonthlyInstallment());
            monthi.setPrincipalAmount(monthi.getCumulativeInstallmentPaid()-monthi.getCumulativeInterestPaid());
            monthi.setInterestPercentage(monthi.getCumulativeInterestPaid()/monthi.getCumulativeInstallmentPaid());
            monthi.setPrincipalPercentage(1-monthi.getInterestPercentage());
        }
    }
    List<MonthlyValues> generateMonthlyPvOfCf(MonthlyCashFlow monthlyCashFlow) {
        List<MonthlyValues> monthlyValuesList = new ArrayList<>();
        MonthlyValues firstMonth = new MonthlyValues();
        generateFirstMonthValues(firstMonth, monthlyCashFlow);
        monthlyValuesList.add(firstMonth);
        for(int i=1;i<=monthlyCashFlow.getNumberOfMidMonths();i++){
            MonthlyValues monthi= new MonthlyValues();
            monthi.setMonthNo(i+1);
            monthi.setMonthlyInstallment(monthlyCashFlow.getBaseRent()*monthlyCashFlow.getMidMonthDays());
            monthi.setCumulativeInstallmentPaid(monthlyValuesList.get(i-1).getCumulativeInstallmentPaid()+monthi.getMonthlyInstallment());
            monthi.setPvOfCf(monthi.getMonthlyInstallment()/(Math.pow((1+monthlyCashFlow.getInterestRate()),monthi.getMonthNo())));
            monthlyValuesList.add(monthi);
        }
        MonthlyValues lastMonth = new MonthlyValues();
        generateLastMonthValues(lastMonth, monthlyCashFlow, monthlyValuesList.get(monthlyValuesList.size()-1));
        monthlyValuesList.add(lastMonth);
        return monthlyValuesList;
    }
    private void generateLastMonthValues(MonthlyValues lastMonth, MonthlyCashFlow monthlyCashFlow, MonthlyValues previousMonth) {
        lastMonth.setMonthNo(monthlyCashFlow.getNumberOfMidMonths()+2);
        lastMonth.setMonthlyInstallment(monthlyCashFlow.getBaseRent()*monthlyCashFlow.getLastMonthDays());
        lastMonth.setCumulativeInstallmentPaid(previousMonth.getCumulativeInstallmentPaid()+lastMonth.getMonthlyInstallment());
        lastMonth.setPvOfCf(lastMonth.getMonthlyInstallment()/(Math.pow((1+monthlyCashFlow.getInterestRate()),lastMonth.getMonthNo())));
    }
    private void generateFirstMonthValues(MonthlyValues firstMonth, MonthlyCashFlow monthlyCashFlow) {
        firstMonth.setMonthNo(1);
        firstMonth.setMonthlyInstallment(monthlyCashFlow.getBaseRent()*monthlyCashFlow.getFirstMonthDays());
        firstMonth.setCumulativeInstallmentPaid(firstMonth.getMonthlyInstallment());
        firstMonth.setPvOfCf(firstMonth.getMonthlyInstallment()/(Math.pow((1+monthlyCashFlow.getInterestRate()),firstMonth.getMonthNo())));
    }
    void setCalculatedValues(MonthlyCashFlow monthlyCashFlow) {
        monthlyCashFlow.setNumberOfMidMonths(((monthlyCashFlow.getTenure()-monthlyCashFlow.getFirstMonthDays())/monthlyCashFlow.getMidMonthDays()));
        monthlyCashFlow.setLastMonthDays(monthlyCashFlow.getTenure()-monthlyCashFlow.getFirstMonthDays()-(monthlyCashFlow.getMidMonthDays()*monthlyCashFlow.getNumberOfMidMonths()));
        monthlyCashFlow.setFirstMonthInstallment(monthlyCashFlow.getFirstMonthDays()*monthlyCashFlow.getBaseRent());
        monthlyCashFlow.setMidMonthsInstallment(monthlyCashFlow.getMidMonthDays()*monthlyCashFlow.getBaseRent());
        monthlyCashFlow.setLastMonthInstallment(monthlyCashFlow.getLastMonthDays()*monthlyCashFlow.getBaseRent());
    }
    void setDefaults(MonthlyCashFlow monthlyCashFlow){
        monthlyCashFlow.setFirstMonthDays(13);
        monthlyCashFlow.setMidMonthDays(26);
    }
}