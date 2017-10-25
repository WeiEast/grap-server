package com.treefinance.saas.grapserver.common.model.vo.demo.fund;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by haojiahong on 2017/10/17.
 */
public class FundUserInfoVO implements Serializable {

    private static final long serialVersionUID = -2404030527309070384L;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出生日期
     */
    private String birthday;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 客户号
     */
    private String customerNumber;

    /**
     * 公积金号
     */
    private String gjjNumber;

    /**
     * 账户余额(包含公积金余额跟补贴余额)
     */
    private BigDecimal balance;

    /**
     * 公积金账户余额
     */
    private BigDecimal fundBalance;

    /**
     * 补贴公积金账户余额(补贴公积金)
     */
    private BigDecimal subsidyBalance;

    /**
     * 补贴月缴存
     */
    private BigDecimal subsidyIncome;

    /**
     * 缴存状态
     */
    private String payStatus;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 证件类型
     */
    private String cardType;

    /**
     * 通讯地址
     */
    private String homeAddress;

    /**
     * 企业账户号码
     */
    private String corporationNumber;

    /**
     * 当前缴存企业名称
     */
    private String corporationName;

    /**
     * 企业月度缴存
     */
    private BigDecimal monthlyCorporationIncome;

    /**
     * 个人月度缴存
     */
    private BigDecimal monthlyCustomerIncome;

    /**
     * 月度总缴存
     */
    private BigDecimal monthlyTotalIncome;

    /**
     * 公司缴存比例
     */
    private String corporationRatio;

    /**
     * 个人缴存比例
     */
    private String customerRatio;

    /**
     * 补贴公积金个人缴存比例
     */
    private String subsidyCustomerRatio;

    /**
     * 补贴公积金公司缴存比例
     */
    private String subsidyCorporationRatio;

    /**
     * 缴存基数
     */
    private BigDecimal baseNumber;

    /**
     * 开户时间
     */
    private String beginDate;

    /**
     * 最新缴存时间
     */
    private String lastPayDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getGjjNumber() {
        return gjjNumber;
    }

    public void setGjjNumber(String gjjNumber) {
        this.gjjNumber = gjjNumber;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getCorporationNumber() {
        return corporationNumber;
    }

    public void setCorporationNumber(String corporationNumber) {
        this.corporationNumber = corporationNumber;
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

    public String getSubsidyCustomerRatio() {
        return subsidyCustomerRatio;
    }

    public void setSubsidyCustomerRatio(String subsidyCustomerRatio) {
        this.subsidyCustomerRatio = subsidyCustomerRatio;
    }

    public String getSubsidyCorporationRatio() {
        return subsidyCorporationRatio;
    }

    public void setSubsidyCorporationRatio(String subsidyCorporationRatio) {
        this.subsidyCorporationRatio = subsidyCorporationRatio;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getLastPayDate() {
        return lastPayDate;
    }

    public void setLastPayDate(String lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getFundBalance() {
        return fundBalance;
    }

    public void setFundBalance(BigDecimal fundBalance) {
        this.fundBalance = fundBalance;
    }

    public BigDecimal getSubsidyBalance() {
        return subsidyBalance;
    }

    public void setSubsidyBalance(BigDecimal subsidyBalance) {
        this.subsidyBalance = subsidyBalance;
    }

    public BigDecimal getSubsidyIncome() {
        return subsidyIncome;
    }

    public void setSubsidyIncome(BigDecimal subsidyIncome) {
        this.subsidyIncome = subsidyIncome;
    }

    public BigDecimal getMonthlyCorporationIncome() {
        return monthlyCorporationIncome;
    }

    public void setMonthlyCorporationIncome(BigDecimal monthlyCorporationIncome) {
        this.monthlyCorporationIncome = monthlyCorporationIncome;
    }

    public BigDecimal getMonthlyCustomerIncome() {
        return monthlyCustomerIncome;
    }

    public void setMonthlyCustomerIncome(BigDecimal monthlyCustomerIncome) {
        this.monthlyCustomerIncome = monthlyCustomerIncome;
    }

    public BigDecimal getMonthlyTotalIncome() {
        return monthlyTotalIncome;
    }

    public void setMonthlyTotalIncome(BigDecimal monthlyTotalIncome) {
        this.monthlyTotalIncome = monthlyTotalIncome;
    }

    public BigDecimal getBaseNumber() {
        return baseNumber;
    }

    public void setBaseNumber(BigDecimal baseNumber) {
        this.baseNumber = baseNumber;
    }
}
