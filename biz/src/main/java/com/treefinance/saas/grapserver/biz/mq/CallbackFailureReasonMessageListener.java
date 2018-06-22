package com.treefinance.saas.grapserver.biz.mq;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.assistant.model.TaskCallbackFailureReasonMessage;
import com.treefinance.saas.assistant.plugin.rocketmq.producer.MonitorMessageProducer;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.model.dto.CallbackFailureReasonDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;
import com.treefinance.saas.grapserver.dao.mapper.TaskCallbackLogUpdateMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 * <p>
 * 接受爬数指明的回调失败具体原因,
 * 方便统计出回调是个人原因还是系统或网站的原因.
 *
 * @author haojiahong
 * @date 2018/6/11
 */
@Component
public class CallbackFailureReasonMessageListener extends AbstractRocketMqMessageListener {

    @Autowired
    private TaskCallbackLogUpdateMapper taskCallbackLogUpdateMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private MonitorMessageProducer monitorMessageProducer;

    private static final Logger logger = LoggerFactory.getLogger(CallbackFailureReasonMessageListener.class);

    @Override
    protected void handleMessage(String message) {
        if (StringUtils.isBlank(message)) {
            logger.error("接收爬数发送的回调失败具体原因为空,message={}", message);
            throw new IllegalArgumentException("接收爬数发送的回调失败具体原因为空,message=" + message);
        }
        CallbackFailureReasonDTO callbackFailureReasonDTO = JSON.parseObject(message, CallbackFailureReasonDTO.class);
        //更新回调日志表
        TaskCallbackLog taskCallbackLog = new TaskCallbackLog();
        taskCallbackLog.setId(UidGenerator.getId());
        taskCallbackLog.setTaskId(callbackFailureReasonDTO.getTaskId());
        taskCallbackLog.setConfigId(callbackFailureReasonDTO.getCallbackConfigId());
        taskCallbackLog.setFailureReason(callbackFailureReasonDTO.getFailureReason());
        taskCallbackLogUpdateMapper.insertOrUpdateSelective(taskCallbackLog);
        //发送监控消息
        TaskDTO taskDTO = taskService.getById(callbackFailureReasonDTO.getTaskId());
        if (!EBizType.OPERATOR.getCode().equals(taskDTO.getBizType())) {
            logger.info("接收爬数发送的回调失败具体原因,任务类型非运营商,暂不处理.message={},task={}", message, JSON.toJSONString(taskDTO));
            return;
        }
        TaskCallbackFailureReasonMessage taskCallbackFailureReasonMessage
                = DataConverterUtils.convert(taskDTO, TaskCallbackFailureReasonMessage.class);
        taskCallbackFailureReasonMessage.setTaskId(taskDTO.getId());
        taskCallbackFailureReasonMessage.setDataTime(taskDTO.getCreateTime());
        taskCallbackFailureReasonMessage.setFailureReason(callbackFailureReasonDTO.getFailureReason());
        List<TaskAttribute> attributeList = taskAttributeService.findByTaskId(callbackFailureReasonDTO.getTaskId());
        Map<String, String> attributeMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(attributeList)) {
            attributeList.forEach(taskAttribute -> attributeMap.put(taskAttribute.getName(), taskAttribute.getValue()));
        }
        taskCallbackFailureReasonMessage.setTaskAttributes(attributeMap);
        monitorMessageProducer.send(taskCallbackFailureReasonMessage);
        logger.info("send task callback failure reason to saas-monitor,message={}", JSON.toJSONString(taskCallbackFailureReasonMessage));
    }
}
