package com.treefinance.saas.grapserver; /**
 * Copyright © 2017 Treefinance All Rights Reserved
 */

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.biz.common.SpringUtils;
import com.treefinance.saas.grapserver.biz.service.AppCallbackConfigService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.TaskTimeService;
import com.treefinance.saas.grapserver.common.model.dto.AppCallbackConfigDTO;
import com.treefinance.saas.grapserver.facade.enums.EDataType;
import com.treefinance.saas.grapserver.facade.model.MerchantBaseInfoRO;
import com.treefinance.saas.grapserver.facade.model.TaskRO;
import com.treefinance.saas.grapserver.facade.service.MerchantFacade;
import com.treefinance.saas.grapserver.facade.service.TaskFacade;
import com.treefinance.saas.grapserver.web.GrapServerApplication;
import com.treefinance.saas.knife.result.SaasResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private MerchantFacade merchantFacade;

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private TaskTimeService taskTimeService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testUpdateAccountNo() {
        taskService.setAccountNo(67627606253551616l, "test");
        System.out.println("------");
    }

    @Test
    public void testA() {
        List<AppCallbackConfigDTO> list = appCallbackConfigService.getByAppIdAndBizType("QATestabcdefghQA", (byte) 4, EDataType.DELIVERY_ADDRESS);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void testMerchantFacade() {
        Long taskId = 0L;
        SaasResult<MerchantBaseInfoRO> result = merchantFacade.getMerchantBaseInfoByTaskId(taskId);
        System.out.println(JSON.toJSONString(result));


    }

    @Test
    public void TestTaskFacade_getById() {
        SaasResult<TaskRO> result = taskFacade.getById(129891186650411008L);
        System.out.println(JSON.toJSONString(result));
    }

    @Test
    public void testTaskTimeService() {
        Long taskId = 137964901845987328L;
        taskTimeService.handleTaskTimeout(taskId);

    }


    @Test
    public void testRedisLock() throws InterruptedException {
        String key = "1234567";
        RedisKeyTask redisKeyTask = new RedisKeyTask(key, 20000L);
        Thread thread1 = new Thread(redisKeyTask);
        Thread thread2 = new Thread(redisKeyTask);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

    }

    public class RedisKeyTask implements Runnable {
        private RedisDao redisDao;
        private String lockKey;
        private Long expire;

        RedisKeyTask(String lockKey, Long expire) {
            this.redisDao = (RedisDao) SpringUtils.getBean("redisDao");
            this.lockKey = lockKey;
            this.expire = expire;
        }

        @Override
        public void run() {
            Map<String, Object> lockMap = new HashMap<>();
            try {
                lockMap = redisDao.acquireLock(lockKey, expire);
                if (lockMap != null) {
                    System.out.println(Thread.currentThread().getName() + "执行业务逻辑");
                    Thread.sleep(10 * 1000);//获得锁，执行业务逻辑方法
                    System.out.println("业务逻辑执行完成");
                } else {
                    System.out.println(Thread.currentThread().getName() + "未获取到锁");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisDao.releaseLock(lockKey, lockMap, expire);
            }
        }
    }

    @Test
    public void testRediskey() throws InterruptedException {
        String key = "aaa-test:123456";
        redisTemplate.opsForValue().set(key, 2 + "");
        if (redisTemplate.getExpire(key) == -1) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
        Thread.sleep(10 * 1000);
        redisTemplate.delete(key);
        System.out.println("haodone ========");
    }


}
