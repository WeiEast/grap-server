package com.treefinance.saas.grapserver.common.model.vo.demo.fund;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by haojiahong on 2017/10/17.
 */
public class FundLoanInfoVO implements Serializable {

    private static final long serialVersionUID = 1188702309440138471L;

    /**
     * 借款人姓名
     */
    private String name;

    /**
     * 借款人身份证
     */
    private String idCard;

    /**
     * 借款人手机
     */
    private String phone;

    /**
     * 借款人联系地址
     */
    private String mailingAddress;

    /**
     * 合同号
     */
    private String contractNumber;

    /**
     * 借据状态
     */
    private String status;

    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;

    /**
     * 借款期数
     */
    private Integer periods;

    /**
     * 房屋地址
     */
    private String houseAddress;

    /**
     * 承办银行
     */
    private String bank;

    /**
     * 贷款类型
     */
    private String loanType;

    /**
     * 贷款发生日期
     */
    private String startDate;

    /**
     * 贷款结束日期
     */
    private String endDate;


    /**
     * 还款方式
     */
    private String repayType;

    /**
     * 扣款日
     */
    private int deductDay;

    /**
     * 月还款额
     */
    private BigDecimal monthlyRepayAmount;

    /**
     * 扣款账号
     */
    private String bankAccount;

    /**
     * 扣款帐户名称
     */
    private String bankAccountName;

    /**
     * 贷款利率
     */
    private String loanInterestPercent;

    /**
     * 罚息利率
     */
    private String penaltyInterestPercent;

    /**
     * 商贷合同编号
     */
    private String commercialContractNumber;

    /**
     * 商贷银行
     */
    private String commercialBank;

    /**
     * 商业贷款金额
     */
    private BigDecimal commercialAmount;

    /**
     * 第二还款人银行账号
     */
    private String secondBankAccount;

    /**
     * 第二还款人姓名
     */
    private String secondBankAccountName;

    /**
     * 第二还款人身份证
     */
    private String secondIdCard;

    /**
     * 第二还款人手机
     */
    private String secondPhone;

    /**
     * 第二还款人工作单位
     */
    private String secondCorporationName;

    /**
     * 贷款余额
     */
    private BigDecimal remainAmount;

    /**
     * 剩余期数
     */
    private Integer remainPeriods;

    /**
     * 最后还款日期
     */
    private String lastRepayDate;

    /**
     * 逾期本金
     */
    private BigDecimal overdueCapital;

    /**
     * 逾期利息
     */
    private BigDecimal overdueInterest;

    /**
     * 逾期罚息
     */
    private BigDecimal overduePenalty;

    /**
     * 逾期天数
     */
    private Integer overdueDays;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPeriods() {
        return periods;
    }

    public void setPeriods(Integer periods) {
        this.periods = periods;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRepayType() {
        return repayType;
    }

    public void setRepayType(String repayType) {
        this.repayType = repayType;
    }

    public int getDeductDay() {
        return deductDay;
    }

    public void setDeductDay(int deductDay) {
        this.deductDay = deductDay;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getLoanInterestPercent() {
        return loanInterestPercent;
    }

    public void setLoanInterestPercent(String loanInterestPercent) {
        this.loanInterestPercent = loanInterestPercent;
    }

    public String getPenaltyInterestPercent() {
        return penaltyInterestPercent;
    }

    public void setPenaltyInterestPercent(String penaltyInterestPercent) {
        this.penaltyInterestPercent = penaltyInterestPercent;
    }

    public String getCommercialContractNumber() {
        return commercialContractNumber;
    }

    public void setCommercialContractNumber(String commercialContractNumber) {
        this.commercialContractNumber = commercialContractNumber;
    }

    public String getCommercialBank() {
        return commercialBank;
    }

    public void setCommercialBank(String commercialBank) {
        this.commercialBank = commercialBank;
    }

    public String getSecondBankAccount() {
        return secondBankAccount;
    }

    public void setSecondBankAccount(String secondBankAccount) {
        this.secondBankAccount = secondBankAccount;
    }

    public String getSecondBankAccountName() {
        return secondBankAccountName;
    }

    public void setSecondBankAccountName(String secondBankAccountName) {
        this.secondBankAccountName = secondBankAccountName;
    }

    public String getSecondIdCard() {
        return secondIdCard;
    }

    public void setSecondIdCard(String secondIdCard) {
        this.secondIdCard = secondIdCard;
    }

    public String getSecondPhone() {
        return secondPhone;
    }

    public void setSecondPhone(String secondPhone) {
        this.secondPhone = secondPhone;
    }

    public String getSecondCorporationName() {
        return secondCorporationName;
    }

    public void setSecondCorporationName(String secondCorporationName) {
        this.secondCorporationName = secondCorporationName;
    }

    public Integer getRemainPeriods() {
        return remainPeriods;
    }

    public void setRemainPeriods(Integer remainPeriods) {
        this.remainPeriods = remainPeriods;
    }

    public String getLastRepayDate() {
        return lastRepayDate;
    }

    public void setLastRepayDate(String lastRepayDate) {
        this.lastRepayDate = lastRepayDate;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getMonthlyRepayAmount() {
        return monthlyRepayAmount;
    }

    public void setMonthlyRepayAmount(BigDecimal monthlyRepayAmount) {
        this.monthlyRepayAmount = monthlyRepayAmount;
    }

    public BigDecimal getCommercialAmount() {
        return commercialAmount;
    }

    public void setCommercialAmount(BigDecimal commercialAmount) {
        this.commercialAmount = commercialAmount;
    }

    public BigDecimal getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(BigDecimal remainAmount) {
        this.remainAmount = remainAmount;
    }

    public BigDecimal getOverdueCapital() {
        return overdueCapital;
    }

    public void setOverdueCapital(BigDecimal overdueCapital) {
        this.overdueCapital = overdueCapital;
    }

    public BigDecimal getOverdueInterest() {
        return overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getOverduePenalty() {
        return overduePenalty;
    }

    public void setOverduePenalty(BigDecimal overduePenalty) {
        this.overduePenalty = overduePenalty;
    }
}
