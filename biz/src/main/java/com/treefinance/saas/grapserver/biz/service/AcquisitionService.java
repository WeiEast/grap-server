package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.taskcenter.facade.request.AcquisitionRequest;
import com.treefinance.saas.taskcenter.facade.service.AcquisitionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author luoyihua on 2017/5/10.
 */
@Service
public class AcquisitionService {

    private static final Logger logger = LoggerFactory.getLogger(AcquisitionService.class);

    @Autowired
    private AcquisitionFacade acquisitionFacade;

    public void acquisition(Long taskid, String header, String cookie, String url, String website, String accountNo, String topic) {
        logger.info("acquisition : taskid={},header={},cookie={},url={},website={},accountNo={}",
                taskid, header, cookie, url, website, accountNo);
        AcquisitionRequest rpcRequest = new AcquisitionRequest();
        rpcRequest.setTaskId(taskid);
        rpcRequest.setHeader(header);
        rpcRequest.setCookie(cookie);
        rpcRequest.setUrl(url);
        rpcRequest.setWebsite(website);
        rpcRequest.setAccountNo(accountNo);
        rpcRequest.setTopic(topic);
        acquisitionFacade.acquisition(rpcRequest);
    }

}
