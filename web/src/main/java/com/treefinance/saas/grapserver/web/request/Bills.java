package com.treefinance.saas.grapserver.web.request;

import lombok.Data;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/21下午9:13
 */

public class Bills implements Serializable {
    private Long id;

    /**
     * 邮件id
     */
    private String mailId;

    @Nonnull
    private Short bankId;

    /**
     * 网信给的数据没有
     */
    private Integer userId;

    /**
     * 邮箱地址
     */
    private String emailAccount;

    private String nameOnCard;

    private Byte credibleFlag;

    private Date billStartDate;

    private String cardNums;

    @Nonnull
    private Date billDate;

    @Nonnull
    private Date paymentDueDate;

    @Nonnull
    private Double creditLimit;

    private Double fcCreditLimit;

    @Nonnull
    private Double newBalance;

    private Double fcNewBalance;

    @Nonnull
    private Double minPayment;

    private Double fcMinPayment;

    @Nonnull
    private Double lastBalance;

    private Double fcLastBalance;

    private Double lastPayment;

    private Double fcLastPayment;

    private Double newCharges;

    private Double fcNewCharges;

    private Double adjustment;

    private Double fcAdjustment;

    private Double interest;

    private Double fcInterest;

    private Integer lastIntegral;

    private Integer integral;

    private Integer integralAdd;

    private Integer integralAdjust;

    private Integer integralUsed;

    private List<BillDetails> billDetails;

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    @Nonnull
    public Short getBankId() {
        return bankId;
    }

    public void setBankId(@Nonnull Short bankId) {
        this.bankId = bankId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmailAccount() {
        return emailAccount;
    }

    public void setEmailAccount(String emailAccount) {
        this.emailAccount = emailAccount;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public Byte getCredibleFlag() {
        return credibleFlag;
    }

    public void setCredibleFlag(Byte credibleFlag) {
        this.credibleFlag = credibleFlag;
    }

    public Date getBillStartDate() {
        return billStartDate;
    }

    public void setBillStartDate(Date billStartDate) {
        this.billStartDate = billStartDate;
    }

    public String getCardNums() {
        return cardNums;
    }

    public void setCardNums(String cardNums) {
        this.cardNums = cardNums;
    }

    @Nonnull
    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(@Nonnull Date billDate) {
        this.billDate = billDate;
    }

    @Nonnull
    public Date getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(@Nonnull Date paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    @Nonnull
    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(@Nonnull Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getFcCreditLimit() {
        return fcCreditLimit;
    }

    public void setFcCreditLimit(Double fcCreditLimit) {
        this.fcCreditLimit = fcCreditLimit;
    }

    @Nonnull
    public Double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(@Nonnull Double newBalance) {
        this.newBalance = newBalance;
    }

    public Double getFcNewBalance() {
        return fcNewBalance;
    }

    public void setFcNewBalance(Double fcNewBalance) {
        this.fcNewBalance = fcNewBalance;
    }

    @Nonnull
    public Double getMinPayment() {
        return minPayment;
    }

    public void setMinPayment(@Nonnull Double minPayment) {
        this.minPayment = minPayment;
    }

    public Double getFcMinPayment() {
        return fcMinPayment;
    }

    public void setFcMinPayment(Double fcMinPayment) {
        this.fcMinPayment = fcMinPayment;
    }

    @Nonnull
    public Double getLastBalance() {
        return lastBalance;
    }

    public void setLastBalance(@Nonnull Double lastBalance) {
        this.lastBalance = lastBalance;
    }

    public Double getFcLastBalance() {
        return fcLastBalance;
    }

    public void setFcLastBalance(Double fcLastBalance) {
        this.fcLastBalance = fcLastBalance;
    }

    public Double getLastPayment() {
        return lastPayment;
    }

    public void setLastPayment(Double lastPayment) {
        this.lastPayment = lastPayment;
    }

    public Double getFcLastPayment() {
        return fcLastPayment;
    }

    public void setFcLastPayment(Double fcLastPayment) {
        this.fcLastPayment = fcLastPayment;
    }

    public Double getNewCharges() {
        return newCharges;
    }

    public void setNewCharges(Double newCharges) {
        this.newCharges = newCharges;
    }

    public Double getFcNewCharges() {
        return fcNewCharges;
    }

    public void setFcNewCharges(Double fcNewCharges) {
        this.fcNewCharges = fcNewCharges;
    }

    public Double getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Double adjustment) {
        this.adjustment = adjustment;
    }

    public Double getFcAdjustment() {
        return fcAdjustment;
    }

    public void setFcAdjustment(Double fcAdjustment) {
        this.fcAdjustment = fcAdjustment;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getFcInterest() {
        return fcInterest;
    }

    public void setFcInterest(Double fcInterest) {
        this.fcInterest = fcInterest;
    }

    public Integer getLastIntegral() {
        return lastIntegral;
    }

    public void setLastIntegral(Integer lastIntegral) {
        this.lastIntegral = lastIntegral;
    }

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getIntegralAdd() {
        return integralAdd;
    }

    public void setIntegralAdd(Integer integralAdd) {
        this.integralAdd = integralAdd;
    }

    public Integer getIntegralAdjust() {
        return integralAdjust;
    }

    public void setIntegralAdjust(Integer integralAdjust) {
        this.integralAdjust = integralAdjust;
    }

    public Integer getIntegralUsed() {
        return integralUsed;
    }

    public void setIntegralUsed(Integer integralUsed) {
        this.integralUsed = integralUsed;
    }

    public List<BillDetails> getBillDetails() {
        return billDetails;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBillDetails(List<BillDetails> billDetails) {
        this.billDetails = billDetails;
    }
}
