package com.treefinance.saas.merchantcenter;


import com.treefinance.saas.grapserver.biz.service.AppBizTypeService;
import com.treefinance.saas.grapserver.biz.service.AppCallbackConfigService;
import com.treefinance.saas.grapserver.biz.service.OperatorExtendLoginService;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.biz.dto.AppBizType;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import com.treefinance.saas.knife.result.SaasResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2018/4/8下午2:57
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MerchantCenterTest {

    private static final Logger logger = LoggerFactory.getLogger(MerchantCenterTest.class);
    @Autowired
    OperatorExtendLoginService operatorExtendLoginService;

    @Autowired
    MerchantFacade merchantFacade;

    @Autowired
    AppCallbackConfigService appCalllbackConfigService;


    @Autowired
    AppBizTypeService appBizTypeService;


    @Test
    public void TestMerchantConfig() {
        long taskId = new Long(2100000146);

        SaasResult<MerchantBaseInfoRO> merchantBaseInfoROSaasResult = merchantFacade.getMerchantBaseInfoByTaskId(taskId);
        if (merchantBaseInfoROSaasResult.isSuccess()) {
            logger.info("成功，信息为{}", merchantBaseInfoROSaasResult.getData().toString());
        } else {
            logger.info("null");
        }
    }

    @Test
    public void TestAppCallbackConfig() {
        Byte bytes = new Byte("1");
        List<AppCallbackConfigDTO> dtoList = appCalllbackConfigService.getByAppIdAndBizType("bFRjwJJL1stVBrqP", bytes, EDataType.DELIVERY_ADDRESS);
        logger.info("测试AppCallbackConfig为{}", dtoList.toString());

    }

    @Test
    public void TestAppBizType() {
        Byte bizType = new Byte("1");

        AppBizType appBizType = appBizTypeService.getAppBizType(bizType);
        logger.info("测试AppBizType为{}", appBizType.toString());

    }


}
