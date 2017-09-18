package com.treefinance.saas.grapserver.biz.service.moxie.directive;

import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;

/**
 * 魔蝎指令处理Service
 */
public interface MoxieDirectiveService {
    /**
     * 处理指令
     *
     * @param moxieDirectiveDTO
     */
    void process(MoxieDirectiveDTO moxieDirectiveDTO);
}
