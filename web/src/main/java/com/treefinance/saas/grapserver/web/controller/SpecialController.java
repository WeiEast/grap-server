package com.treefinance.saas.grapserver.web.controller;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.google.common.collect.Maps;
import com.treefinance.saas.knife.result.SimpleResult;
import com.treefinance.saas.grapserver.common.enums.EQRResult;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by luyuan on 2017/5/2.
 */

@RestController
public class SpecialController {

    private static final Logger logger = LoggerFactory.getLogger(SpecialController.class);

    @Autowired
    private CrawlerService crawlerService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;

    @RequestMapping(value = "/qrscan/checking", method = {RequestMethod.POST})
    public Object qrCheck(
            @RequestParam() String directiveId,
            @RequestParam("taskid") Long taskid) throws Exception {
        HttpResult<String> result = crawlerService.verifyQr(directiveId,taskid, null);
        logger.info("taskId={},directiveId={}, 二维码验证结果={}", taskid,directiveId, JSON.toJSONString(result));
        if (StringUtils.isNotEmpty(result.getData()) && (result.getData().equals(EQRResult.FAILED.toString()) ||
                result.getData().equals(EQRResult.SUCCESS.toString()) ||
                result.getData().equals(EQRResult.SKIP.toString()))) {
            if (!taskNextDirectiveService.isTaskCompleted(taskid)){
                taskNextDirectiveService.deleteNextDirective(taskid, null);
            }
        }
        Map<String, Object> map = Maps.newHashMap();
        map.put("result", result.getData());
        return new SimpleResult<>(map);
    }
}
