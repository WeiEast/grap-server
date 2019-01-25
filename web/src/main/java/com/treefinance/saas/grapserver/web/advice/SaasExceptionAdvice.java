package com.treefinance.saas.grapserver.web.advice;

import com.treefinance.saas.grapserver.exception.TaskTimeOutException;
import com.treefinance.saas.grapserver.common.result.SaasResult;
import com.treefinance.saas.grapserver.exception.ForbiddenException;
import com.treefinance.saas.grapserver.exception.IllegalRequestParameterException;
import com.treefinance.saas.grapserver.exception.InformException;
import com.treefinance.saas.grapserver.web.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ValidationException;

/**
 * @author guoguoyun
 * @date 2018/10/30上午9:46
 */

@ControllerAdvice("com.treefinance.saas.grapserver.web.saascontroller")
public class SaasExceptionAdvice extends BaseResponseEntityExceptionHandler<SaasResult> {

    @Override
    protected SaasResult buildBody(Exception ex, WebRequest request) {
        ResponseCode responseCode;
        if (ex instanceof ValidationException || ex instanceof javax.xml.bind.ValidationException || ex instanceof IllegalRequestParameterException || ex instanceof ServletRequestBindingException) {
            responseCode = ResponseCode.ILLEGAL_PARAMETERS;
        } else if (ex instanceof ForbiddenException) {
            responseCode = ResponseCode.FORBIDDEN;
        } else if (ex instanceof TaskTimeOutException) {
            responseCode = ResponseCode.TASK_TIMEOUT;
        } else {
            responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        }

        String errorMsg = null;
        if (ex instanceof ValidationException || ex instanceof javax.xml.bind.ValidationException || ex instanceof InformException) {
            errorMsg = ex.getMessage();
        }
        errorMsg = StringUtils.defaultIfEmpty(errorMsg, responseCode.getMsg());

        SaasResult result = SaasResult.failure(responseCode.getValue(), errorMsg);
        logger.error("Exception handler result={}", result, ex);

        return result;
    }

}
