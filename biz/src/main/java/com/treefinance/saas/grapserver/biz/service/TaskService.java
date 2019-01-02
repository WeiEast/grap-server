package com.treefinance.saas.grapserver.biz.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author hanif
 */
public interface TaskService {

    /**
     * 获取任务的用户ID
     * @param taskId 任务ID
     * @return 用户ID
     */
    String getUniqueIdInTask(Long taskId);

    /**
     * 创建任务
     * @param uniqueId 用户ID
     * @param appId    商户ID
     * @param bizType  业务类型
     * @param extra    额外信息
     * @param website  网站标识
     * @param source   来源
     * @return 任务ID
     */
    Long createTask(String uniqueId, String appId, Byte bizType, String extra, String website, String source);

    /**
     * 更新网站标识
     * @param taskId  任务ID
     * @param website 网站标识
     */
    void updateWebsite(Long taskId, String website);

    /**
     * 根据需要更新记录账号和网站标识
     * @param taskId    任务ID
     * @param accountNo 账号
     * @param website   网站标识
     */
    void recordAccountNoAndWebsite(@Nonnull Long taskId, @Nullable String accountNo, @Nullable String website);

    /**
     * 正常流程下取消任务
     * @param taskId 任务id
     */
    void cancelTask(@Nonnull Long taskId);
}
