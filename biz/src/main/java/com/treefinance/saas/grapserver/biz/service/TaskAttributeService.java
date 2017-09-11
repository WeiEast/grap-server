/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by chenjh on 2017/7/5.
 * <p>
 * 任务拓展属性业务层
 */
@Service
public class TaskAttributeService {
    @Resource
    private TaskAttributeMapper taskAttributeMapper;
    @Autowired
    private ISecurityCryptoService securityCryptoService;

    /**
     * 保存属性
     *
     * @param taskId
     * @param name
     * @param value
     * @return
     */
    public Long insert(Long taskId, String name, String value) {
        long id = UidGenerator.getId();
        TaskAttribute target = new TaskAttribute();
        target.setId(id);
        target.setTaskId(taskId);
        target.setName(name);
        target.setValue(value);
        taskAttributeMapper.insert(target);
        return id;
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
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameEqualTo(name);
        List<TaskAttribute> attributeList = taskAttributeMapper.selectByExample(criteria);
        TaskAttribute taskAttribute = CollectionUtils.isEmpty(attributeList) ? null : attributeList.get(0);
        if (taskAttribute == null) {
            return taskAttribute;
        }
        if (decrypt && StringUtils.isNotEmpty(taskAttribute.getValue())) {
            taskAttribute.setValue(securityCryptoService.decrypt(taskAttribute.getValue(), EncryptionIntensityEnum.NORMAL));
        }
        return taskAttribute;
    }

}
