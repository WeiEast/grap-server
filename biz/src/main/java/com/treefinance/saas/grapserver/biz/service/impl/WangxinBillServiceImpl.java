package com.treefinance.saas.grapserver.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.biz.service.LicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.WangxinBillService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.ETongdunData;
import com.treefinance.saas.grapserver.common.model.dto.carinfo.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.common.result.TongdunData;
import com.treefinance.saas.grapserver.context.config.DiamondConfig;
import com.treefinance.saas.grapserver.util.TongdunDataResolver;
import com.treefinance.saas.riskdataclean.facade.request.BankBillRequest;
import com.treefinance.saas.riskdataclean.facade.result.CustomerHistoryOutputResult;
import com.treefinance.saas.riskdataclean.facade.service.RiskDataFacade;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.treefinance.b2b.saas.util.BeanUtils.convert;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/21下午8:50
 */
@Service
public class WangxinBillServiceImpl implements WangxinBillService {

    private static final Logger logger = LoggerFactory.getLogger(WangxinBillService.class);

    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private RiskDataFacade riskDataFacade;
    @Autowired
    private CarInfoFacade carInfoFacade;
    @Autowired
    private LicenseService licenseService;
    @Autowired
    private TaskLogFacade taskLogFacade;
    @Autowired
    private TaskLogService taskLogService;

    @Override
    public Object clean(Long taskId, String appId, BankBillRequest bankBillRequest) {

        CustomerHistoryOutputResult customerHistoryOutputResult = new CustomerHistoryOutputResult();
        try {
            customerHistoryOutputResult = riskDataFacade.cleanData(bankBillRequest);

        } catch (Exception e) {
            logger.error("调用网信账单洗数任务异常:taskId={},tongdunRequset={}", taskId, bankBillRequest, e);
            processFailCollectTask(taskId, "调用网信账单洗数打分任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
//        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(customerHistoryOutputResult, license.getServerPublicKey());

    }

    public void updateCollectTaskStatusAndTaskLogAndSendMonitor(Long taskId, List<CarInfoCollectTaskLogDTO> logList) {
        List<CarInfoCollectTaskLogRequest> carInfoCollectTaskLogRequestList = convert(logList, CarInfoCollectTaskLogRequest.class);
        carInfoFacade.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogRequestList);
    }

    private void processFailCollectTask(Long taskId, String failMsg) {
        List<CarInfoCollectTaskLogDTO> carInfoCollectTaskLogDTOList = Lists.newArrayList();
        carInfoCollectTaskLogDTOList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_FAIL.getText(), failMsg, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, carInfoCollectTaskLogDTOList);
    }

    private Boolean processSuccessCollectTask(Long taskId, String resultLog) {
        List<CarInfoCollectTaskLogDTO> logList = Lists.newArrayList();
        logList.add(new CarInfoCollectTaskLogDTO(ETaskStep.TASK_SUCCESS.getText(), resultLog, new Date()));
        this.updateCollectTaskStatusAndTaskLogAndSendMonitor(taskId, logList);
        for (CarInfoCollectTaskLogDTO log : logList) {
            if (StringUtils.equalsIgnoreCase(log.getMsg(), ETaskStep.TASK_SUCCESS.getText())) {
                return true;
            }
        }
        return false;
    }

}
