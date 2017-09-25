package com.treefinance.saas.grapserver; /**
 * Copyright © 2017 Treefinance All Rights Reserved
 */

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.AppCallbackConfigService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by chenjh on 2017/6/26.
 * <p>
 * TaskService测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TaskServiceTest {
    @BeforeClass
    public static void before() {
        System.out.println("start test");
    }

    @Autowired
    private AppCallbackConfigService appCallbackConfigService;

    @Autowired
    private TaskService taskService;

    @Test
    public void testUpdateAccountNo() {
        taskService.setAccountNo(67627606253551616l, "test");
        System.out.println("------");
    }

    @Test
    public void testA() {
        List<AppCallbackConfigDTO> list = appCallbackConfigService.getByAppIdAndBizType("QATestabcdefghQA", (byte) 4);
        System.out.println(JSON.toJSONString(list));
    }
}
