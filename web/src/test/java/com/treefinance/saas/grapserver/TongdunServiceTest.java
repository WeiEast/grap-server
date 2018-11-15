package com.treefinance.saas.grapserver;

import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.TongdunService;
import com.treefinance.saas.grapserver.common.request.TongdunRequest;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author:guoguoyun
 * @date:Created in 2018/10/18上午11:35
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TongdunServiceTest {


    @Autowired TaskService taskService;
    @Autowired TongdunService tongdunService;
    @Test
    public void test() throws InterruptedException {
//        Long taskId = taskService.createTask("223344", "QATestabcdefghQA", (byte) 8, null, null, null);
        TongdunRequest tongdunRequest = new TongdunRequest();
        tongdunRequest.setIdCard("36073219891001413565");
        tongdunRequest.setTelNum("15871362990");
        tongdunRequest.setUserName("郭国运");
        tongdunService.processCollectDetailTask(1000L, "223344", tongdunRequest);
        Thread.sleep(10000);
    }
}
