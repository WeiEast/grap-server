package com.treefinance.saas.grapserver.common.enums;

/**
 * Created by yh-treefinance on 2017/6/19.
 */
public enum TaskStatusEnum {

    RUNNING {
        @Override
        public Byte getStatus() {
            return 0;
        }

        @Override
        public String getName() {
            return "运行中";
        }
    },

    CANCLE {
        @Override
        public Byte getStatus() {
            return 1;
        }

        @Override
        public String getName() {
            return "取消";
        }
    },
    SUCCESS {
        @Override
        public Byte getStatus() {
            return 2;
        }

        @Override
        public String getName() {
            return "成功";
        }
    },
    FAIL {
        @Override
        public Byte getStatus() {
            return 3;
        }

        @Override
        public String getName() {
            return "失败";
        }
    };

    public abstract Byte getStatus();

    public abstract String getName();
}
