package com.treefinance.saas.grapserver.common.model.vo.demo.fund;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author haojiahong on 2017/10/17.
 */
public class FundLoanRepayRecordVO implements Serializable {

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
    private BigDecimal repayAmount;

    /**
     * 还款本金
     */
    private BigDecimal repayCapital;

    /**
     * 还款利息
     */
    private BigDecimal repayInterest;

    /**
     * 还款罚息
     */
    private BigDecimal repayPenalty;

    /**
     * 贷款合同号
     */
    private String contractNumber;

    @Override
    public String toString() {
        return "FundLoanRepayRecordVO{" +
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

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public BigDecimal getRepayCapital() {
        return repayCapital;
    }

    public void setRepayCapital(BigDecimal repayCapital) {
        this.repayCapital = repayCapital;
    }

    public BigDecimal getRepayInterest() {
        return repayInterest;
    }

    public void setRepayInterest(BigDecimal repayInterest) {
        this.repayInterest = repayInterest;
    }

    public BigDecimal getRepayPenalty() {
        return repayPenalty;
    }

    public void setRepayPenalty(BigDecimal repayPenalty) {
        this.repayPenalty = repayPenalty;
    }

}
