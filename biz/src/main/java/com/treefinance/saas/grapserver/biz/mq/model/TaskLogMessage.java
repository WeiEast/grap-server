package com.treefinance.saas.grapserver.biz.mq.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 * Created by luoyihua on 2017/4/26.
 */
public class TaskLogMessage  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private Long taskId;
        private String msg;
        private Long timestamp;
        private String errorDetail;

        public Long getTaskId() {
            return taskId;
        }

        public void setTaskId(Long taskId) {
            this.taskId = taskId;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public String getErrorDetail() {
            return errorDetail;
        }

        public void setErrorDetail(String errorDetail) {
            this.errorDetail = errorDetail;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, SHORT_PREFIX_STYLE);
        }
}
