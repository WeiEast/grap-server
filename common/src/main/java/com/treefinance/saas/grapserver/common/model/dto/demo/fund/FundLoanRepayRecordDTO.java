package com.treefinance.saas.grapserver.common.model.dto.demo.fund;

import java.io.Serializable;

/**
 * @author haojiahong on 2017/10/17.
 */
public class FundLoanRepayRecordDTO implements Serializable {

    private static final long serialVersionUID = -8762659271824850408L;

    /**
     * 还款日期
     */
    private String repayDate;

    /**
     * 记账日期
     */
    private String accountingDate;

    /**
     * 还款
     */
    private Integer repayAmount;

    /**
     * 还款本金
     */
    private Integer repayCapital;

    /**
     * 还款利息
     */
    private Integer repayInterest;

    /**
     * 还款罚息
     */
    private Integer repayPenalty;

    /**
     * 贷款合同号
     */
    private String contractNumber;

    @Override
    public String toString() {
        return "FundLoanRepayRecordDTO{" +
                "repayDate='" + repayDate + '\'' +
                ", accountingDate='" + accountingDate + '\'' +
                ", repayAmount=" + repayAmount +
                ", repayCapital=" + repayCapital +
                ", repayInterest=" + repayInterest +
                ", repayPenalty=" + repayPenalty +
                ", contractNumber='" + contractNumber + '\'' +
                '}';
    }

    public String getRepayDate() {
        return repayDate;
    }

    public void setRepayDate(String repayDate) {
        this.repayDate = repayDate;
    }

    public String getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate(String accountingDate) {
        this.accountingDate = accountingDate;
    }

    public Integer getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(Integer repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Integer getRepayCapital() {
        return repayCapital;
    }

    public void setRepayCapital(Integer repayCapital) {
        this.repayCapital = repayCapital;
    }

    public Integer getRepayInterest() {
        return repayInterest;
    }

    public void setRepayInterest(Integer repayInterest) {
        this.repayInterest = repayInterest;
    }

    public Integer getRepayPenalty() {
        return repayPenalty;
    }

    public void setRepayPenalty(Integer repayPenalty) {
        this.repayPenalty = repayPenalty;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

}
