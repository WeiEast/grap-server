package com.treefinance.saas.grapserver.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.treefinance.saas.grapserver.biz.service.WebDetectService;

/**
 * @author guimeichao
 * @date 2018/12/12
 */

/**
 * 舆情监控信息处理
 */
@RestController
@RequestMapping("/internal/webdetect")
public class WebDetectController {

    @Autowired
    private WebDetectService webDetectService;

    /**
     * 发起任务
     * 
     * @param appid
     * @param uniqueId
     * @param platform 平台、网站
     * @param extra
     * @return
     */
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public Object createTask(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId,
        @RequestParam("platform") String platform, @RequestParam("extra") String extra) {
        Long taskId = webDetectService.creatTask(appid, uniqueId);
        return webDetectService.startCrawler(taskId, platform, extra);
    }

    /**
     * 获取数据
     * 
     * @param appid
     * @param uniqueId
     * @param saasid
     * @param size
     * @param start
     * @param platform
     * @param entryname
     * @param keyword
     * @return
     */
    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public Object getData(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId,
        @RequestParam("saasid") Long saasid, @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "start", required = false) Long start, @RequestParam(value = "platform") String platform,
        @RequestParam(value = "entryname") String entryname, @RequestParam(value = "keyword") String keyword) {
        return webDetectService.getData(appid, uniqueId, saasid, size, start, platform, entryname, keyword);
    }

    @RequestMapping(value = "/getEnterpriseData", method = RequestMethod.POST)
    public Object getEnterpriseData(@RequestParam String appid, @RequestParam("uniqueId") String uniqueId,
        @RequestParam("saasid") Long saasid, @RequestParam(value = "enterpriseName") String enterpriseName) {
        return webDetectService.getEnterpriseData(appid, uniqueId, saasid, enterpriseName);
    }
}
