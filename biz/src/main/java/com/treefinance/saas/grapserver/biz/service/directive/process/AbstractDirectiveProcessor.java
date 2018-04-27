package com.treefinance.saas.grapserver.biz.service.directive.process;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.grapserver.biz.service.QRCodeAccountNoLogService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.EDirective;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.model.dto.DirectiveDTO;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yh-treefinance on 2017/7/5.
 */
public abstract class AbstractDirectiveProcessor extends CallbackableDirectiveProcessor implements DirectiveProcessor {
    /**
     * logger
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected TaskService taskService;
    @Autowired
    protected TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private QRCodeAccountNoLogService qrCodeAccountNoLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void process(DirectiveDTO directiveDTO) {
        long start = System.currentTimeMillis();
        if (directiveDTO == null || directiveDTO.getTaskId() == null) {
            logger.error("handle directive error : directive or taskId is null, directive={}", JsonUtils.toJsonString(directiveDTO));
            return;
        }
        Long taskId = directiveDTO.getTaskId();
        // 1.转化为指令
        String directiveName = directiveDTO.getDirective();
        EDirective directive = EDirective.directiveOf(directiveName);
        if (directive == null) {
            logger.error("handle directive error : no support the directive of {}, directive={}", directiveName, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 2.初始化任务详细
        TaskDTO taskDTO = directiveDTO.getTask();
        if (taskDTO == null) {
            taskDTO = taskService.getById(taskId);
            if (taskDTO == null) {
                logger.error("handle directive error : taskId={} is not exists, directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
                return;
            }
            directiveDTO.setTask(taskDTO);
        }
        // 3.任务是否是已经完成
        Byte taskStatus = taskDTO.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("handle directive error : the task id={} is completed: directive={}", taskId, JsonUtils.toJsonString(directiveDTO));
            return;
        }
        // 4.处理指令
        try {
            this.doProcess(directive, directiveDTO);
        } finally {
            qrCodeAccountNoLogService.logQRCodeAccountNo(taskId);
            taskNextDirectiveService.insert(taskId, directiveDTO.getDirective());
            taskNextDirectiveService.putNextDirectiveToRedis(taskId, JsonUtils.toJsonString(directiveDTO, "task"));
            logger.info("process directive completed  cost {} ms : directive={}", System.currentTimeMillis() - start, JSON.toJSONString(directiveDTO));
        }
    }

    /**
     * 处理指令
     *
     * @param directive
     * @param directiveDTO
     */
    protected abstract void doProcess(EDirective directive, DirectiveDTO directiveDTO);

}
