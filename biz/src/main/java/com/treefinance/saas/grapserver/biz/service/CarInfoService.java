package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.monitor.MonitorService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.carinfo.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.grapserver.common.utils.HttpClientUtils;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.knife.result.SimpleResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Good Luck Bro , No Bug !
 * 车辆信息采集处理类
 *
 * @author haojiahong
 * @date 2018/5/31
 */
@Service
public class CarInfoService {

    private final static Logger logger = LoggerFactory.getLogger(CarInfoService.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskLicenseService taskLicenseService;
    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private AppLicenseService appLicenseService;
    @Autowired
    private DiamondConfig diamondConfig;

    /**
     * 创建车辆信息采集任务
     *
     * @param appId    商户id
     * @param modelNum 车型编码
     * @return
     */
    public Long startCollectTask(String appId, String modelNum) {
        //使用车型编码当作uniqueId
        taskLicenseService.verifyCreateTask(appId, modelNum, EBizType.CAR_INFO);
        Long taskId = taskService.createTask(modelNum, appId, EBizType.CAR_INFO.getCode(), null, null, null);
        return taskId;
    }

    /**
     * 调用爬数处理车辆信息采集任务,并更新任务状态记录任务日志,并发送任务监控信息
     *
     * @param taskId   任务id
     * @param appId    商户id
     * @param modelNum 车型编码
     * @return
     */
    public Object processCollectTask(Long taskId, String appId, String modelNum) {
        String url = diamondConfig.getCrawlerUrlCarInfoCollect();
        logger.info("调用爬数处理车辆信息采集任务传入参数:taskId={},modelNum={}", taskId, modelNum);
        Map<String, Object> map = Maps.newHashMap();
        map.put("modelNum", modelNum);
        String httpResult;
        try {
            httpResult = HttpClientUtils.doPost(url, map);
        } catch (Exception e) {
            logger.error("调用爬数处理车辆信息采集任务异常:taskId={},modelNum={}", taskId, modelNum, e);
            processFailCollectTask(taskId, "调用爬数处理车辆信息采集任务异常");
            return SimpleResult.failResult("车辆信息采集失败");
        }
        JSONObject result = JSON.parseObject(httpResult);
        if (result.get("resultLog") != null
                && StringUtils.isNotBlank(result.get("resultLog").toString())
                && checkResultLog(result.get("resultLog").toString())) {
            processSuccessCollectTask(taskId, result.get("resultLog").toString());
            result.remove("resultLog");
            AppLicense license = appLicenseService.getAppLicense(appId);
            return SimpleResult.successEncryptByRSAResult(result, license.getServerPublicKey());

        } else {
            logger.error("调用爬数处理车辆信息采集任务返回值中任务日志信息存在问题:taskId={},modelNum={},httpResult={},result={}",
                    taskId, modelNum, httpResult, JSON.toJSONString(result));
            processFailCollectTask(taskId, "调用爬数处理车辆信息采集任务返回值中任务日志信息存在问题");
            return SimpleResult.failResult("车辆信息采集失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        for (CarInfoCollectTaskLogDTO log : logList) {
            taskLogService.insert(taskId, log.getMsg(), log.getOccurTime(), log.getErrorMsg());
            //任务成功
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                taskService.updateTaskStatus(taskId, ETaskStatus.SUCCESS.getStatus());
            }
            //任务失败
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_FAIL.getText())) {
                taskService.updateTaskStatus(taskId, ETaskStatus.FAIL.getStatus());
            }
        }
        monitorService.sendMonitorMessage(taskId);
    }


    /**
     * 校验调用爬数处理车辆信息采集任务返回值中任务日志信息是否存在问题
     * 若日志列表中,没有表明任务结束的日志,则存在问题
     *
     * @param resultLog
     * @return
     */
    private boolean checkResultLog(String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = JSON.parseArray(resultLog, CarInfoCollectTaskLogDTO.class);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())
                    || StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_FAIL.getText())) {
                return true;
            }
        }
        return false;
    }

    private void processFailCollectTask(Long taskId, String failMsg) {
        List<CarInfoCollectTaskLogDTO> carInfoCollectTaskLogDTOList = Lists.newArrayList();
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.CRAWL_FAIL.getText(), failMsg, new Date()));
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_FAIL.getText(), null, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogDTOList);
    }

    private Boolean processSuccessCollectTask(Long taskId, String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = JSON.parseArray(resultLog, CarInfoCollectTaskLogDTO.class);
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, logList);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                return true;
            }
        }
        return false;
    }


}
