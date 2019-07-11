package com.treefinance.saas.grapserver.biz.service;

import com.datatrees.spider.share.domain.CommonPluginParam;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
public interface EnterpriseInformationService {

    Object prepare(Long taskId, String extra);

    Object startCrawler(CommonPluginParam commonPluginParam);

    boolean isStartCrawler(String enterpriseName);

    Object getEnterpriseData(String appId, String uniqueId, Long taskid, String enterpriseName);

    Object getResult(String enterpriseName);

    Object startPynerCrawler(Long taskid,String platform, String extra);
}
