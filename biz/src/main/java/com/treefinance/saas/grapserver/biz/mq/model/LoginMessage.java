package com.treefinance.saas.grapserver.biz.mq.model;

import java.io.Serializable;

/**
 * Created by luyuan on 2017/5/3.
 */
public class LoginMessage implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * 站点名
     */
    private String websiteName;

    /**
     * 任务ID
     */
    private long   taskId;

    /**
     * 结束url
     */
    private String endUrl;

    /**
     * head配置:cookie
     */
    private String cookie;

    /**
     * head配置项:Set-Cookie
     */
    private String setCookie;

    private String accountNo;

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getEndUrl() {
        return endUrl;
    }

    public void setEndUrl(String endUrl) {
        this.endUrl = endUrl;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getSetCookie() {
        return setCookie;
    }

    public void setSetCookie(String setCookie) {
        this.setCookie = setCookie;
    }

    /**
     * @return the accountNo
     */
    public String getAccountNo() {
        return accountNo;
    }

    /**
     * @param accountNo the accountNo to set
     */
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

}
