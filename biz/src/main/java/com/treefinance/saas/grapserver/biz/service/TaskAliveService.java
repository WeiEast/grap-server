package com.treefinance.saas.grapserver.biz.service;

/**
 * Buddha Bless , No Bug !
 * 任务活跃服务类
 *
 * @author haojiahong
 * @date 2018/4/24
 */

public interface TaskAliveService {

    /**
     * 更新任务最近活跃时间 可能存在多个请求同时更新活跃时间,未获得锁的请求可过滤掉
     *
     * @param taskId 任务id
     */
    void updateTaskActiveTime(Long taskId);
    /**
     * 获取任务最近活跃时间
     */
    String getTaskAliveTime(Long taskId);
}
