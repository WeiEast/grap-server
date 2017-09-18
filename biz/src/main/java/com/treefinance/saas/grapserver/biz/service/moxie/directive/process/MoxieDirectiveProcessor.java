package com.treefinance.saas.grapserver.biz.service.moxie.directive.process;

import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;

public interface MoxieDirectiveProcessor {

    /**
     * 处理消息
     *
     * @param directiveDTO
     */
    void process(MoxieDirectiveDTO directiveDTO);
}
