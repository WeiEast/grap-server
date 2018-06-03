package com.treefinance.saas.grapserver.biz.service.monitor;

import com.treefinance.saas.grapserver.biz.service.thread.MonitorMessageSendThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Created by yh-treefinance on 2017/6/20.
 */
@Service
public class MonitorService {
    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    @Autowired
    private TaskCallbackMsgMonitorService taskCallbackMsgMonitorService;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolExecutor;


    /**
     * 发送监控消息
     *
     * @param taskId 任务id
     */
    public void sendMonitorMessage(Long taskId) {
        // 事务提交之后，发送消息(如果事务回滚,任务可能未更新为完成状态,不需要发送监控消息)
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                logger.info("TransactionSynchronizationManager: afterCommit taskId={}", taskId);
                threadPoolExecutor.execute(new MonitorMessageSendThread(taskId));
            }
        });
    }


    /**
     * 发送回调信息监控消息
     *
     * @param taskId
     * @param httpCode
     * @param result
     */
    @Async
    public void sendTaskCallbackMsgMonitorMessage(Long taskId, Integer httpCode, String result, Boolean isCallback) {
        taskCallbackMsgMonitorService.sendMessage(taskId, httpCode, result, isCallback);
        logger.info("sendTaskCallbackMsgMonitorMessage:taskId={},httpCode={},result={},isCallback={}", taskId, httpCode, result, isCallback);
    }
}
