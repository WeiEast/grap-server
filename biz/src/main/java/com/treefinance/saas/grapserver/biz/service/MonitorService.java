package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.assistant.model.HttpMonitorMessage;

/**
 * @author yh-treefinance on 2017/6/20.
 */
public interface MonitorService {


    void pushHttpMonitorMessage(HttpMonitorMessage message);
}
