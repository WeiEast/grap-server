/**
 * Copyright © 2017 Treefinance All Rights Reserved
 */
package com.treefinance.saas.grapserver.biz.service;

import com.google.common.collect.Maps;
import com.treefinance.basicservice.security.crypto.facade.EncryptionIntensityEnum;
import com.treefinance.basicservice.security.crypto.facade.ISecurityCryptoService;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskAttributeCriteria;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeMapper;
import com.treefinance.saas.grapserver.dao.mapper.TaskAttributeUpdateMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private TaskAttributeUpdateMapper taskAttributeUpdateMapper;
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
     * 保存或更新属性
     *
     * @param taskId
     * @param name
     * @param value
     */
    public void insertOrUpdateSelective(Long taskId, String name, String value) {
        long id = UidGenerator.getId();
        TaskAttribute target = new TaskAttribute();
        target.setId(id);
        target.setTaskId(taskId);
        target.setName(name);
        target.setValue(value);
        taskAttributeUpdateMapper.insertOrUpdateSelective(target);
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

    /**
     * 批量通过属性名查询属性值
     *
     * @param taskId
     * @param decrypt
     * @param names
     * @return
     */
    public Map<String, TaskAttribute> findByNames(Long taskId, boolean decrypt, String... names) {
        List<String> namelist = Arrays.asList(names);
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andTaskIdEqualTo(taskId).andNameIn(namelist);
        List<TaskAttribute> attributeList = taskAttributeMapper.selectByExample(criteria);

        Map<String, TaskAttribute> attributeMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(attributeList)) {
            for (TaskAttribute taskAttribute : attributeList) {
                if (decrypt && StringUtils.isNotEmpty(taskAttribute.getValue())) {
                    taskAttribute.setValue(securityCryptoService.decrypt(taskAttribute.getValue(), EncryptionIntensityEnum.NORMAL));
                }
                attributeMap.put(taskAttribute.getName(), taskAttribute);
            }
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
        if (encrypt) {
            value = securityCryptoService.encrypt(value, EncryptionIntensityEnum.NORMAL);
        }
        TaskAttributeCriteria criteria = new TaskAttributeCriteria();
        criteria.createCriteria().andNameEqualTo(name).andValueEqualTo(value);
        List<TaskAttribute> attributeList = taskAttributeMapper.selectByExample(criteria);
        TaskAttribute taskAttribute = CollectionUtils.isEmpty(attributeList) ? null : attributeList.get(0);
        if (taskAttribute == null) {
            return null;
        }
        return taskAttribute;
    }

}
