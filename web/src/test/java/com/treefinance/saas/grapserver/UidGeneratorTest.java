package com.treefinance.saas.grapserver; /**
 * Copyright © 2017 Treefinance All Rights Reserved
 */

import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by chenjh on 2017/6/26.
 * <p>
 * Uid生成工具测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = GrapServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UidGeneratorTest {
    @BeforeClass
    public static void before() {
        System.out.println("start test");
    }

    @Test
    public void getId() {
        long id = UidGenerator.getId();
        Assert.assertNotNull(id);
        System.out.println("生成的ID为：" + id);
    }

    @Test
    public void getIdList() {
        int size = 10;
        long[] ids = UidGenerator.getIds(size);
        Assert.assertNotNull(ids);
        System.out.println("生成的ID集合为：" + ArrayUtils.toString(ids));
    }
}
