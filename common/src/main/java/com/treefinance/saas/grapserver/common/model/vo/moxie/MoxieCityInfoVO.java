package com.treefinance.saas.grapserver.common.model.vo.moxie;

import java.io.Serializable;
import java.util.List;

/**
 * @author haojiahong on 2017/9/13.
 */
public class MoxieCityInfoVO implements Serializable {

    private static final long serialVersionUID = -8737716430802940041L;

    private String spell;

    private String label;

    private String value;

    private String status;

    private List<MoxieCityInfoVO> list;

    @Override
    public String toString() {
        return "MoxieCityInfoVO{" +
                "spell='" + spell + '\'' +
                ", label='" + label + '\'' +
                ", value='" + value + '\'' +
                ", status='" + status + '\'' +
                ", list=" + list +
                '}';
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MoxieCityInfoVO> getList() {
        return list;
    }

    public void setList(List<MoxieCityInfoVO> list) {
        this.list = list;
    }

}
