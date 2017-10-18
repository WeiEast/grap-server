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
}
