package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.riskdataclean.facade.request.BankBillRequest;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/21下午8:50
 */
public interface WangxinBillService {
    Object clean(Long taskId, String appId, BankBillRequest bankBillRequest);
}
