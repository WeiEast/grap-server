package com.treefinance.saas.grapserver;

import com.treefinance.saas.grapserver.biz.service.LicenseService;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author chengtong
 * @date 18/4/12 16:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LicenseServiceTest {

    @Resource
    private LicenseService licenseService;


    @Test
    public void getAppLicense() {
        licenseService.getAppLicense("RgZn1TRtkrp6h5mm");
    }


}