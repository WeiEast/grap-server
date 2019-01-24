package com.treefinance.saas.grapserver.biz.service;

/**
 * @author guimeichao
 * @date 2018/12/12
 */
public interface WebDetectService {

    Object startCrawler(Long taskid, String platform, String extra);

    Object getData(String appId, String uniqueId, Long taskid, Integer size, Long start, String platform,
        String entryname, String keyword);
}
