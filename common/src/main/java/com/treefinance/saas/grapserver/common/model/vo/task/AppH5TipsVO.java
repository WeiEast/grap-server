package com.treefinance.saas.grapserver.common.model.vo.task;

import java.io.Serializable;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/5/8
 */
public class AppH5TipsVO implements Serializable {

    private static final long serialVersionUID = 8392665843518446944L;

    /**
     * 提示类型
     */
    private Byte tipsType;
    /**
     * 提示信息
     */
    private String tipsContent;


    public Byte getTipsType() {
        return tipsType;
    }

    public void setTipsType(Byte tipsType) {
        this.tipsType = tipsType;
    }

    public String getTipsContent() {
        return tipsContent;
    }

    public void setTipsContent(String tipsContent) {
        this.tipsContent = tipsContent;
    }
}
