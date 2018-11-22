package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * @author haojiahong on 2017/9/13.
 */
public class MoxieCityInfoDTO implements Serializable {

    private static final long serialVersionUID = -2896899478417809806L;

    private String status;

    private String areaCode;

    private String cityName;

    private String province;

    @Override
    public String toString() {
        return "MoxieCityInfoDTO{" +
                "status='" + status + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", province='" + province + '\'' +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}
