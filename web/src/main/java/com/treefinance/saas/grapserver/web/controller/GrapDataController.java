package com.treefinance.saas.grapserver.web.controller;

import com.treefinance.saas.grapserver.biz.service.GrapDataDownloadService;
import com.treefinance.saas.grapserver.common.model.vo.GrapDataVO;
import com.treefinance.saas.knife.result.SimpleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by yh-treefinance on 2018/2/7.
 */
@RestController
public class GrapDataController {
    @Autowired
    private GrapDataDownloadService grapDataDownloadService;

    @RequestMapping(value = {"/grap/{bizType}/data"}, method = {RequestMethod.GET})
    public Object getData(@PathVariable("bizType") String bizType, GrapDataVO grapDataVO) {
        return SimpleResult.successResult(grapDataDownloadService.getEncryptGrapData(grapDataVO.getAppid(), bizType, grapDataVO.getTaskId()));
    }
}
