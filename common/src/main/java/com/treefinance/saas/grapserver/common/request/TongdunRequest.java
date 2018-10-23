package com.treefinance.saas.grapserver.common.request;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18上午10:08
 */
public class TongdunRequest {
    /**姓名**/
    private String userName;
    /**身份证号**/
    private String idCard;
    /**手机号**/
    private String telNum;
    /**邮箱**/
    private String accountEmail;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    @Override public String toString() {
        return "TongdunRequest{" + "userName='" + userName + '\'' + ", idCard='" + idCard + '\'' + ", telNum='" + telNum
            + '\'' + ", accountEmail='" + accountEmail + '\'' + '}';
    }
}
