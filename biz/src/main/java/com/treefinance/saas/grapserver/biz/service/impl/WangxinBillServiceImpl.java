package com.treefinance.saas.grapserver.biz.service.impl;

import com.google.common.collect.Lists;
import com.treefinance.saas.grapserver.biz.domain.AppLicense;
import com.treefinance.saas.grapserver.biz.service.LicenseService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.WangxinBillService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.model.dto.carinfo.CarInfoCollectTaskLogDTO;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.riskdataclean.facade.request.BankBillRequest;
import com.treefinance.saas.riskdataclean.facade.result.RiskDataResult;
import com.treefinance.saas.riskdataclean.facade.service.RiskDataFacade;
import com.treefinance.saas.taskcenter.facade.request.CarInfoCollectTaskLogRequest;
import com.treefinance.saas.taskcenter.facade.service.CarInfoFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskFacade;
import com.treefinance.saas.taskcenter.facade.service.TaskLogFacade;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        List<RiskDataResult>  riskDataResults = new ArrayList<>();
        try {
             riskDataResults = riskDataFacade.cleanData(bankBillRequest);

        } catch (Exception e) {
            logger.error("调用网信账单洗数任务异常:taskId={},tongdunRequset={}", taskId, bankBillRequest, e);
            processFailCollectTask(taskId, "调用网信账单洗数打分任务异常");
            return SaasResult.failResult("Unexpected exception!");
        }

        AppLicense license = licenseService.getAppLicense(appId);
        processSuccessCollectTask(taskId, "任务成功");
        return SaasResult.successEncryptByRSAResult(riskDataResults, license.getServerPublicKey());

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
