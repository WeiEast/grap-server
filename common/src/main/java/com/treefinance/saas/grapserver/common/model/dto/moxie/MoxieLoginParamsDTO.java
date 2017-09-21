package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/9/21.
 */
public class MoxieLoginParamsDTO implements Serializable {
    private static final long serialVersionUID = 2496811170928273528L;

    private String uniqueId;
    private String areaCode;
    private String account;
    private String password;
    private String loginType;
    private String idCard;
    private String mobile;
    private String realName;
    private String subArea;
    private String loanAccount;
    private String loanPassword;
    private String corpAccount;
    private String corpName;
    private String origin;
    private String ip;


    public MoxieLoginParamsDTO() {

    }

    public MoxieLoginParamsDTO(String uniqueId, String areaCode, String account, String password, String loginType,
                               String idCard, String mobile, String realName, String subArea, String loanAccount,
                               String loanPassword, String corpAccount, String corpName, String origin, String ip) {
        this.uniqueId = uniqueId;
        this.areaCode = areaCode;
        this.account = account;
        this.password = password;
        this.loginType = loginType;
        this.idCard = idCard;
        this.mobile = mobile;
        this.realName = realName;
        this.subArea = subArea;
        this.loanAccount = loanAccount;
        this.loanPassword = loanPassword;
        this.corpAccount = corpAccount;
        this.corpName = corpName;
        this.origin = origin;
        this.ip = ip;

    }


    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSubArea() {
        return subArea;
    }

    public void setSubArea(String subArea) {
        this.subArea = subArea;
    }

    public String getLoanAccount() {
        return loanAccount;
    }

    public void setLoanAccount(String loanAccount) {
        this.loanAccount = loanAccount;
    }

    public String getLoanPassword() {
        return loanPassword;
    }

    public void setLoanPassword(String loanPassword) {
        this.loanPassword = loanPassword;
    }

    public String getCorpAccount() {
        return corpAccount;
    }

    public void setCorpAccount(String corpAccount) {
        this.corpAccount = corpAccount;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
