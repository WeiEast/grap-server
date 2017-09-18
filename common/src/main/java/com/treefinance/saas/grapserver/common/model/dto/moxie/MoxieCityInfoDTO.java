package com.treefinance.saas.grapserver.common.model.dto.moxie;

import java.io.Serializable;

/**
 * Created by haojiahong on 2017/9/13.
 */
public class MoxieCityInfoDTO implements Serializable {


    private static final long serialVersionUID = -2896899478417809806L;

    private String status;
    private String area_code;
    private String city_name;
    private String province;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArea_code() {
        return area_code;
    }

    public void setArea_code(String area_code) {
        this.area_code = area_code;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
