package com.treefinance.saas.grapserver;

import com.treefinance.saas.grapserver.biz.service.AppLicenseService;
import com.treefinance.saas.grapserver.biz.dto.AppLicense;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;

/**
 * @author chengtong
 * @date 18/4/12 16:25
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AppLicenseServiceTest {

    @Resource
    AppLicenseService appLicenseService;


    @Test
    public void getAppLicense() {
        appLicenseService.getAppLicense("RgZn1TRtkrp6h5mm");
    }

    @Test
    public void setAppLicense() {
        AppLicense appLicense = new AppLicense();

        appLicense.setAppId("cttest");
        appLicense.setCreateTime(new Date());
        appLicense.setDataSecretKey("testDataSecretKey");
        appLicense.setLastUpdateTime(new Date());
        appLicense.setSdkPrivateKey("testSdkPrivateKey");
        appLicense.setServerPrivateKey("testServerPrivateKey");
        appLicense.setSdkPublicKey("testSdkPublicKey");
        appLicense.setServerPublicKey("testServerPublicKey");
        appLicense.setId(1);

//        appLicenseService.setAppLicense(appLicense);

    }

    @Test
    public void getDataKey() {
    }

    @Test
    public void getCallbackLicense() {
//        appLicenseService.getCallbackLicense(31);

    }
}