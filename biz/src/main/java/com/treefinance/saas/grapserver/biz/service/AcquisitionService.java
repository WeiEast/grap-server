package com.treefinance.saas.grapserver.biz.service;

import org.springframework.stereotype.Service;

/**
 * @author luoyihua on 2017/5/10.
 */
@Service
public interface AcquisitionService {


    void acquisition(Long taskId, String header, String cookie, String url, String website,
        String accountNo, String topic);

    void acquisition(Long taskId, String header, String cookie, String url, String website,
            String accountNo, String topic, String extra);
}
