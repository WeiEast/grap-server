package com.treefinance.saas.grapserver.biz.service.directive;

import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;

/**
 * 指令消息处理Service
 * Created by yh-treefinance on 2017/7/5.
 */
public interface DirectiveService {
    /**
     * 处理消息
     *
     * @param directiveDTO
     */
   void process(DirectiveDTO directiveDTO);
}
