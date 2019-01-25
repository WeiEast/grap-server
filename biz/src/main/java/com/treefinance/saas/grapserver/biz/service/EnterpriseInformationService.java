package com.treefinance.saas.grapserver.biz.service;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
public interface EnterpriseInformationService {

    Object startCrawler(Long taskid, String extra);

    boolean isStartCrawler(String enterpriseName);

    Object getEnterpriseData(String appId, String uniqueId, Long taskid, String enterpriseName);

    Object getResult(String enterpriseName);

    Object startPynerCrawler(Long taskid,String platform, String extra);
}
