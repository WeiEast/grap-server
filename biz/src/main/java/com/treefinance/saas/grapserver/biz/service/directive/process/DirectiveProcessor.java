package com.treefinance.saas.grapserver.biz.service.directive.process;

import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;

/**
 * 指令消息处理器
 * Created by yh-treefinance on 2017/7/6.
 */
public interface DirectiveProcessor {

    /**
     * 处理消息
     *
     * @param directiveDTO
     */
    void process(DirectiveDTO directiveDTO);
}
