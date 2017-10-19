package com.treefinance.saas.grapserver.common.model.dto.demo.fund;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/10/17.
 */
public class FundBillRecordDTO implements Serializable {

    private static final long serialVersionUID = -6281155542842167705L;

    /**
     * 出账
     */
    private Integer outcome;

    /**
     * 入账
     */
    private Integer income;

    /**
     * 缴存信息描述
     */
    private String description;

    /**
     * 补贴入账
     */
    private Integer subsidyIncome;

    /**
     * 补贴出账
     */
    private Integer subsidyOutcome;

    /**
     * 余额
     */
    private Integer balance;

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
    private Integer corporationIncome;

    /**
     * 个人缴存金额
     */
    private Integer customerIncome;

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
    private Integer additionalIncome;

    public Integer getOutcome() {
        return outcome;
    }

    public void setOutcome(Integer outcome) {
        this.outcome = outcome;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSubsidyIncome() {
        return subsidyIncome;
    }

    public void setSubsidyIncome(Integer subsidyIncome) {
        this.subsidyIncome = subsidyIncome;
    }

    public Integer getSubsidyOutcome() {
        return subsidyOutcome;
    }

    public void setSubsidyOutcome(Integer subsidyOutcome) {
        this.subsidyOutcome = subsidyOutcome;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
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

    public Integer getCorporationIncome() {
        return corporationIncome;
    }

    public void setCorporationIncome(Integer corporationIncome) {
        this.corporationIncome = corporationIncome;
    }

    public Integer getCustomerIncome() {
        return customerIncome;
    }

    public void setCustomerIncome(Integer customerIncome) {
        this.customerIncome = customerIncome;
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

    public Integer getAdditionalIncome() {
        return additionalIncome;
    }

    public void setAdditionalIncome(Integer additionalIncome) {
        this.additionalIncome = additionalIncome;
    }
}
