package com.treefinance.saas.grapserver.common.model.vo.demo.fund;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author haojiahong on 2017/10/17.
 */
public class FundBillRecordVO implements Serializable {

    private static final long serialVersionUID = -6281155542842167705L;

    /**
     * 出账
     */
    private BigDecimal outcome;

    /**
     * 入账
     */
    private BigDecimal income;

    /**
     * 缴存信息描述
     */
    private String description;

    /**
     * 补贴入账
     */
    private BigDecimal subsidyIncome;

    /**
     * 补贴出账
     */
    private BigDecimal subsidyOutcome;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 缴存时间
     */
    private String dealTime;

    /**
     * 缴存年月
     */
    private String month;
    /**
     * 缴存公司名称
     */
    private String corporationName;

    /**
     * 公司缴存金额
     */
    private BigDecimal corporationIncome;

    /**
     * 个人缴存金额
     */
    private BigDecimal customerIncome;

    /**
     * 公司缴存比例
     */
    private String corporationRatio;

    /**
     * 个人缴存比例
     */
    private String customerRatio;

    /**
     * 补缴
     */
    private BigDecimal additionalIncome;

    @Override
    public String toString() {
        return "FundBillRecordVO{" +
                "outcome=" + outcome +
                ", income=" + income +
                ", description='" + description + '\'' +
                ", subsidyIncome=" + subsidyIncome +
                ", subsidyOutcome=" + subsidyOutcome +
                ", balance=" + balance +
                ", dealTime='" + dealTime + '\'' +
                ", month='" + month + '\'' +
                ", corporationName='" + corporationName + '\'' +
                ", corporationIncome=" + corporationIncome +
                ", customerIncome=" + customerIncome +
                ", corporationRatio='" + corporationRatio + '\'' +
                ", customerRatio='" + customerRatio + '\'' +
                ", additionalIncome=" + additionalIncome +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCorporationName() {
        return corporationName;
    }

    public void setCorporationName(String corporationName) {
        this.corporationName = corporationName;
    }

    public String getCorporationRatio() {
        return corporationRatio;
    }

    public void setCorporationRatio(String corporationRatio) {
        this.corporationRatio = corporationRatio;
    }

    public String getCustomerRatio() {
        return customerRatio;
    }

    public void setCustomerRatio(String customerRatio) {
        this.customerRatio = customerRatio;
    }

    public BigDecimal getOutcome() {
        return outcome;
    }

    public void setOutcome(BigDecimal outcome) {
        this.outcome = outcome;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getSubsidyIncome() {
        return subsidyIncome;
    }

    public void setSubsidyIncome(BigDecimal subsidyIncome) {
        this.subsidyIncome = subsidyIncome;
    }

    public BigDecimal getSubsidyOutcome() {
        return subsidyOutcome;
    }

    public void setSubsidyOutcome(BigDecimal subsidyOutcome) {
        this.subsidyOutcome = subsidyOutcome;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCorporationIncome() {
        return corporationIncome;
    }

    public void setCorporationIncome(BigDecimal corporationIncome) {
        this.corporationIncome = corporationIncome;
    }

    public BigDecimal getCustomerIncome() {
        return customerIncome;
    }

    public void setCustomerIncome(BigDecimal customerIncome) {
        this.customerIncome = customerIncome;
    }

    public BigDecimal getAdditionalIncome() {
        return additionalIncome;
    }

    public void setAdditionalIncome(BigDecimal additionalIncome) {
        this.additionalIncome = additionalIncome;
    }

}
