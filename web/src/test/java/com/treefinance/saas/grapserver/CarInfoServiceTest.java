package com.treefinance.saas.grapserver;

import com.treefinance.saas.grapserver.biz.service.CarInfoService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EBizType;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CarInfoServiceTest {
    @Autowired
    private CarInfoService carInfoService;
    @Autowired
    private TaskService taskService;

    @Test
    public void test() throws InterruptedException {
        Long taskId = taskService.createTask("QATestabcdefghQA","223344", EBizType.CAR_INFO, null, null, null);
        carInfoService.processCollectTask(taskId, "223344", null);
        Thread.sleep(10000);
    }

}
