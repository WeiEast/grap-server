package com.treefinance.saas.grapserver.web.request;

import lombok.Data;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/21下午9:17
 */
public class BillDetails implements Serializable{

    private Short bankId;

    private String cardNo;

    private Date transDate;

    private Date postDate;

    @Nonnull
    private String description;

    private String currencyType;

    private Double amountMoney;

    private Integer cityId;

    public Short getBankId() {
        return bankId;
    }

    public void setBankId(Short bankId) {
        this.bankId = bankId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    @Nonnull public String getDescription() {
        return description;
    }

    public void setDescription(@Nonnull String description) {
        this.description = description;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public Double getAmountMoney() {
        return amountMoney;
    }

    public void setAmountMoney(Double amountMoney) {
        this.amountMoney = amountMoney;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }
}
