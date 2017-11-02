package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.mq.MessageProducer;
import com.treefinance.saas.grapserver.biz.mq.MqConfig;
import com.treefinance.saas.grapserver.biz.mq.model.LoginMessage;
import com.treefinance.saas.grapserver.common.enums.EOperatorCodeType;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * Created by luoyihua on 2017/5/5.
 */
@Service
public class OperatorLoginSimulationService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorLoginSimulationService.class);

    @Resource
    private CrawlerService crawlerService;
    @Autowired
    private MqConfig mqConfig;
    @Autowired
    private MessageProducer messageProducer;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private TaskAttributeService taskAttributeService;

    public String getOperator(String operatorName, String province) {
        operatorName = StringUtils.remove(operatorName, "中国");
        province = province.replace("省", "").replace("市", "").replace("自治区", "").replace("维吾尔", "").replace("壮族", "")
                .replace("回族", "");
        return province + operatorName;
    }

    public void prepare(Long taskId, String websiteName, String accountNo, String groupCode, String groupName) {
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setTaskId(taskId);
        loginMessage.setWebsiteName(websiteName);
        loginMessage.setAccountNo(accountNo);
        loginMessage.setGroupCode(groupCode);
        loginMessage.setGroupName(groupName);
        try {
            messageProducer.send(GsonUtils.toJson(loginMessage), mqConfig.getProviderRawdataTopic(),
                    mqConfig.getProviderRawdataTag(), taskId.toString());
            taskAttributeService.insertOrUpdateSelective(taskId, "groupCode", groupCode);
            taskAttributeService.insertOrUpdateSelective(taskId, "groupName", groupName);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public HttpResult<String> login(Long taskId, String username, String password, String accountNo, String website, String code, String random, Map<String, String> extra) {
        HttpResult<String> result = crawlerService.login(taskId, username, password, code, random, extra);
        taskService.updateTask(taskId, accountNo, website);
        taskTimeService.updateLoginTime(taskId, new Date());
        logger.info("taskId={}, 登录结果={}", taskId, JSON.toJSONString(result));
        return result;
    }

    @SuppressWarnings("rawtypes")
    public Object refreshCode(Long taskId, String type, String username, String password) {
        //refreshLog(taskId, type);
        Map<String, String> paramMap = Maps.newHashMap();
        HttpResult<String> res = crawlerService.fetchLoginCode(taskId, EOperatorCodeType.getCode(type), username,
                password, paramMap);
        Map<String, Object> result = Maps.newHashMap();
        if (type.equals(EOperatorCodeType.QR.getText())) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map qrMap = objectMapper.readValue(res.getData(), Map.class);
                result.put("httpUrl", qrMap.get("httpQRCode"));
                result.put("rpcUrl", qrMap.get("rpcQRCode"));
                return result;
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } else if (type.equals(EOperatorCodeType.IMG.getText())) {
            result.put("captcha", res.getData());
        }
        return result;
    }

    private void refreshLog(Long taskId, String type) {
        if (type.equals(EOperatorCodeType.QR.getText())) {
            taskLogService.insert(taskId, "用户刷新二维码", DateTime.now().toDate(), null);
        } else if (type.equals(EOperatorCodeType.IMG.getText())) {
            taskLogService.insert(taskId, "用户刷新图片验证码", DateTime.now().toDate(), null);
        } else if (type.equals(EOperatorCodeType.SMS.getText())) {
            taskLogService.insert(taskId, "用户刷新短信验证码", DateTime.now().toDate(), null);
        }
    }
}
