package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public interface TaskService {

    /**
     * 处理系统调用
     * 设置任务导入的账号,当且仅当Task表里面的accountNo为null或者为空的时候设置
     *
     * @param taskId
     * @param accountNo
     * @return
     */
    int setAccountNo(Long taskId, String accountNo);

    /**
     * 更新任务状态
     *
     * @param taskId
     * @param status
     * @return
     */
    int updateTaskStatus(Long taskId, Byte status);

    /**
     * 取消任务
     *
     * @param taskId
     * @return
     */
    int cancleTask(Long taskId);

    /**
     * 获取当前taskId对应该平台该用户曾经导过的taskId列表
     *
     * @param taskId
     * @return
     */
    List<Long> getUserTaskIdList(Long taskId);

    /**
     * 通过id获取task记录
     *
     * @param taskId
     * @return
     */
    TaskDTO getById(Long taskId);

    /**
     * 任务是否正在执行
     *
     * @param taskid
     * @return
     */
    boolean isDoingTask(Long taskid);

    /**
     * 任务是否完成
     *
     * @param taskid
     * @return
     */
    boolean isTaskCompleted(Long taskid);

    /**
     * 任务是否超时
     *
     * @param taskid
     * @return
     */
    boolean isTaskTimeout(Long taskid);

    /**
     * 开始任务
     *
     * @param uniqueId
     * @param appid
     * @param type
     * @param deviceInfo
     * @param ipAddress
     * @param coorType
     * @return
     */
    Long startTask(String uniqueId, String appid, EBizType type, String deviceInfo, String ipAddress, String coorType, String extra) throws IOException;

    /**
     * 更新accountNo与website
     *
     * @param taskId
     * @param accountNo
     * @param webSite
     */
    void updateTask(Long taskId, String accountNo, String webSite);

    /**
     * 取消任务并设置任务的取消环节
     *
     * @param id
     * @return
     */
    String cancelTaskWithStep(Long id);

    /**
     * 失败任务并设置任务的失败环节
     *
     * @param id
     * @return
     */
    String failTaskWithStep(Long id);

    /**
     * 任务成功,但可能回调失败,此时需要更改任务状态为失败,并记录日志信息
     *
     * @param taskId
     * @param status
     * @return
     */
    String updateTaskStatusWithStep(Long taskId, Byte status);
}
