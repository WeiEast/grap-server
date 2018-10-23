/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.biz.service;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.common.exception.UnknownException;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAttributeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by chenjh on 2017/7/5.
 * <p>
 * 任务拓展属性业务层
 */
@Service
public class TaskAttributeService {
    @Autowired
    private TaskAttributeFacade taskAttributeFacade;


    /**
     * 保存或更新属性
     *
     * @param taskId
     * @param name
     * @param value
     */
    public void insertOrUpdateSelective(Long taskId, String name, String value) {
        taskAttributeFacade.insertOrUpdateSelective(taskId, name, value);
    }

    /**
     * 通过属性名查询属性值
     *
     * @param taskId
     * @param name    属性名
     * @param decrypt 是否要解密，true:是，false:否
     * @return
     */
    public TaskAttribute findByName(Long taskId, String name, boolean decrypt) {
        TaskResult<TaskAttributeRO> rpcResult = taskAttributeFacade.findByName(taskId, name, decrypt);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");

        }
        if (rpcResult.getData() == null) {
            return null;
        }
        TaskAttribute taskAttribute = DataConverterUtils.convert(rpcResult.getData(), TaskAttribute.class);
        return taskAttribute;
    }

    /**
     * 批量通过属性名查询属性值
     *
     * @param taskId
     * @param decrypt
     * @param names
     * @return
     */
    public Map<String, TaskAttribute> findByNames(Long taskId, boolean decrypt, String... names) {

        TaskResult<Map<String, TaskAttributeRO>> rpcResult = taskAttributeFacade.findByNames(taskId, decrypt, names);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        Map<String, TaskAttribute> attributeMap = Maps.newHashMap();

        for (Map.Entry<String, TaskAttributeRO> taskAttributeROEntry : rpcResult.getData().entrySet()) {
            TaskAttribute taskAttribute = DataConverterUtils.convert(taskAttributeROEntry, TaskAttribute.class);
            attributeMap.put(taskAttributeROEntry.getKey(), taskAttribute);
        }
        return attributeMap;
    }

    /**
     * 通过属性名和属性值查询taskId
     *
     * @param name
     * @param value
     * @param encrypt
     * @return
     */
    public TaskAttribute findByNameAndValue(String name, String value, boolean encrypt) {

        TaskResult<TaskAttributeRO> rpcResult = taskAttributeFacade.findByNameAndValue(name, value, encrypt);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        if (rpcResult.getData() == null) {
            return null;
        }
        TaskAttribute taskAttribute = DataConverterUtils.convert(rpcResult.getData(), TaskAttribute.class);
        return taskAttribute;
    }

    /**
     * 根据taskId查询所有属性
     *
     * @param taskId
     * @return
     */
    public List<TaskAttribute> findByTaskId(Long taskId) {
        TaskResult<List<TaskAttributeRO>> rpcResult = taskAttributeFacade.findByTaskId(taskId);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        List<TaskAttribute> attributeList = DataConverterUtils.convert(rpcResult.getData(), TaskAttribute.class);
        return attributeList;
    }

}
