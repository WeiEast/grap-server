package com.treefinance.saas.grapserver.common.model.dto.demo.fund;

import java.io.Serializable;

/**
 * @author haojiahong on 2017/10/17.
 */
public class FundUserInfoDTO implements Serializable {

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
    private Integer balance;

    /**
     * 公积金账户余额
     */
    private Integer fundBalance;

    /**
     * 补贴公积金账户余额(补贴公积金)
     */
    private Integer subsidyBalance;

    /**
     * 补贴月缴存
     */
    private Integer subsidyIncome;

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
    private Integer monthlyCorporationIncome;

    /**
     * 个人月度缴存
     */
    private Integer monthlyCustomerIncome;

    /**
     * 月度总缴存
     */
    private Integer monthlyTotalIncome;

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
    private Integer baseNumber;

    /**
     * 开户时间
     */
    private String beginDate;

    /**
     * 最新缴存时间
     */
    private String lastPayDate;

    @Override
    public String toString() {
        return "FundUserInfoDTO{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", gjjNumber='" + gjjNumber + '\'' +
                ", balance=" + balance +
                ", fundBalance=" + fundBalance +
                ", subsidyBalance=" + subsidyBalance +
                ", subsidyIncome=" + subsidyIncome +
                ", payStatus='" + payStatus + '\'' +
                ", idCard='" + idCard + '\'' +
                ", cardType='" + cardType + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", corporationNumber='" + corporationNumber + '\'' +
                ", corporationName='" + corporationName + '\'' +
                ", monthlyCorporationIncome=" + monthlyCorporationIncome +
                ", monthlyCustomerIncome=" + monthlyCustomerIncome +
                ", monthlyTotalIncome=" + monthlyTotalIncome +
                ", corporationRatio='" + corporationRatio + '\'' +
                ", customerRatio='" + customerRatio + '\'' +
                ", subsidyCustomerRatio='" + subsidyCustomerRatio + '\'' +
                ", subsidyCorporationRatio='" + subsidyCorporationRatio + '\'' +
                ", baseNumber=" + baseNumber +
                ", beginDate='" + beginDate + '\'' +
                ", lastPayDate='" + lastPayDate + '\'' +
                '}';
    }

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

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getFundBalance() {
        return fundBalance;
    }

    public void setFundBalance(Integer fundBalance) {
        this.fundBalance = fundBalance;
    }

    public Integer getSubsidyBalance() {
        return subsidyBalance;
    }

    public void setSubsidyBalance(Integer subsidyBalance) {
        this.subsidyBalance = subsidyBalance;
    }

    public Integer getSubsidyIncome() {
        return subsidyIncome;
    }

    public void setSubsidyIncome(Integer subsidyIncome) {
        this.subsidyIncome = subsidyIncome;
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

    public Integer getMonthlyCorporationIncome() {
        return monthlyCorporationIncome;
    }

    public void setMonthlyCorporationIncome(Integer monthlyCorporationIncome) {
        this.monthlyCorporationIncome = monthlyCorporationIncome;
    }

    public Integer getMonthlyCustomerIncome() {
        return monthlyCustomerIncome;
    }

    public void setMonthlyCustomerIncome(Integer monthlyCustomerIncome) {
        this.monthlyCustomerIncome = monthlyCustomerIncome;
    }

    public Integer getMonthlyTotalIncome() {
        return monthlyTotalIncome;
    }

    public void setMonthlyTotalIncome(Integer monthlyTotalIncome) {
        this.monthlyTotalIncome = monthlyTotalIncome;
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

    public Integer getBaseNumber() {
        return baseNumber;
    }

    public void setBaseNumber(Integer baseNumber) {
        this.baseNumber = baseNumber;
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

}
