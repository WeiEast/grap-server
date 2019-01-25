package com.treefinance.saas.grapserver.biz.service;

/**
 * Good Luck Bro , No Bug !
 * 车辆信息采集处理类
 *
 * @author haojiahong
 * @date 2018/5/31
 */
public interface CarInfoService {

    /**
     * 调用爬数处理车辆信息采集任务,并更新任务状态记录任务日志,并发送任务监控信息
     *
     * @param taskId 任务id
     * @param appId 商户id
     * @param modelNum 车型编码
     */
    Object processCollectTask(Long taskId, String appId, String modelNum);
}
