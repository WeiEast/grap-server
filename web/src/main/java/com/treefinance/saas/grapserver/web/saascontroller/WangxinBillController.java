package com.treefinance.saas.grapserver.web.saascontroller;

import com.alibaba.fastjson.JSON;
import com.treefinance.b2b.saas.util.BeanUtils;
import com.treefinance.saas.common.model.BankBill;
import com.treefinance.saas.common.model.BankBillDetail;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.WangxinBillService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.context.component.AbstractController;
import com.treefinance.saas.grapserver.web.request.Bills;
import com.treefinance.saas.grapserver.web.request.RiskDataRequest;
import com.treefinance.saas.riskdataclean.facade.request.BankBillRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/21下午8:37
 */

@RestController
@RequestMapping("/bankbill")
public class WangxinBillController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(WangxinBillController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private WangxinBillService wangxinBillService;

    /**
     * 网信账单洗数评分
     */
    @RequestMapping(value = "/risk/dataclean", method = {RequestMethod.POST})
    public Object clean(@RequestBody RiskDataRequest riskDataRequest) {

        Long taskId = initial(riskDataRequest.getAppid(), riskDataRequest.getAppid(), EBizType.BILL_WANGXIN_CLEAN);
        Object result = wangxinBillService.clean(taskId, riskDataRequest.getAppid(), buildRequest(riskDataRequest));
        logger.info("网信账单洗数评分,返回结果:result={},taskId={},appid={}", JSON.toJSONString(result), taskId, riskDataRequest.getAppid());
        return result;
    }

    private Long initial(String appId, String uniqueId, EBizType bizType) {
        logger.info("网信账单洗数评分, appid={},name={},idcard={},mobile={},type={}", appId, uniqueId, bizType);
        // 创建任务, 使用appid当作uniqueId
        return taskService.createTask(appId, uniqueId, bizType);
    }

    private BankBillRequest buildRequest(RiskDataRequest riskDataRequest) {
        BankBillRequest bankBillRequest = new BankBillRequest();
        List<BankBill> bankBillList = new ArrayList<>();
        for (Bills bill : riskDataRequest.getData().getBills()) {
            BankBill bankBill = BeanUtils.convert(bill, BankBill.class);
            bankBill.setBankBillDetailList(BeanUtils.convert(bill.getBillDetails(), BankBillDetail.class));
            bankBillList.add(bankBill);
        }
        bankBillRequest.setBankBillList(bankBillList);

        return bankBillRequest;

    }

}
