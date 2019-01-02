package com.treefinance.saas.grapserver.biz.service;

/**
 * @author guimeichao
 * @date 2018/12/29
 */
public interface EnterpriseInformationService {

    Long creatTask(String appId, String uniqueId);

    Object startCrawler(Long taskid, String extra);

}
