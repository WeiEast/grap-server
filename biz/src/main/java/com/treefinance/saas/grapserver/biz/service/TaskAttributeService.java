package com.treefinance.saas.grapserver.biz.service;

import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.exception.UnknownException;
import com.treefinance.saas.grapserver.biz.dto.TaskAttribute;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.taskcenter.facade.result.TaskAttributeRO;
import com.treefinance.saas.taskcenter.facade.result.common.TaskResult;
import com.treefinance.saas.taskcenter.facade.service.TaskAttributeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author chenjh on 2017/7/5.
 * <p>
 * 任务拓展属性业务层
 */
@Service
public class TaskAttributeService extends AbstractService {

    @Autowired
    private TaskAttributeFacade taskAttributeFacade;

    /**
     * 保存或更新属性
     */
    public void insertOrUpdateSelective(Long taskId, String name, String value) {
        taskAttributeFacade.insertOrUpdateSelective(taskId, name, value);
    }

    /**
     * 通过属性名查询属性值
     *
     * @param taskId  taskId
     * @param name    属性名
     * @param decrypt 是否要解密，true:是，false:否
     * @return        拓展属性对象
     */
    public TaskAttribute findByName(Long taskId, String name, boolean decrypt) {
        TaskResult<TaskAttributeRO> rpcResult = taskAttributeFacade.findByName(taskId, name, decrypt);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");

        }
        if (rpcResult.getData() == null) {
            return null;
        }
        return convert(rpcResult.getData(), TaskAttribute.class);
    }

    /**
     * 批量通过属性名查询属性值
     */
    public Map<String, TaskAttribute> findByNames(Long taskId, boolean decrypt, String... names) {

        TaskResult<Map<String, TaskAttributeRO>> rpcResult = taskAttributeFacade.findByNames(taskId, decrypt, names);
        if (!rpcResult.isSuccess()) {
            throw new UnknownException("调用taskcenter失败");
        }
        Map<String, TaskAttribute> attributeMap = Maps.newHashMap();

        for (Map.Entry<String, TaskAttributeRO> taskAttributeROEntry : rpcResult.getData().entrySet()) {
            TaskAttribute taskAttribute = convert(taskAttributeROEntry, TaskAttribute.class);
            attributeMap.put(taskAttributeROEntry.getKey(), taskAttribute);
        }
        return attributeMap;
    }

}
