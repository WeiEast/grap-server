package com.treefinance.saas.grapserver.common.enums;

import java.util.Objects;

/**
 * 任务详细环节对应编码为：
 * <p>
 * Created by haojiahong on 2017/8/21.
 */
public enum ETaskStep {

    TASK_CREATE("任务创建", "CJ", "创建", "CJ01", "任务创建"),
    WAITING_USER_SCAN_QR_CODE("等待用户扫描二维码", "PS", "爬数", "PS01", "扫描二维码"),
    QR_CODE_VALIDATE_FAIL("二维码验证失败", "PS", "爬数", "PS01", "扫描二维码"),
    QR_CODE_VALIDATE_SUCCESS("二维码验证成功", "PS", "爬数", "PS01", "扫描二维码"),
    WAITING_USER_INPUT_MESSAGE_CODE("等待用户输入短信验证码", "PS", "爬数", "PS02", "短信验证码"),
    SENDING_MESSAGE_CODE("向手机发送短信验证码", "PS", "爬数", "PS02", "短信验证码"),
    MESSAGE_CODE_VALIDATE_FAIL("短信验证码校验失败", "PS", "爬数", "PS02", "短信验证码"),
    MESSAGE_CODE_VALIDATE_SUCCESS("短信验证码校验成功", "PS", "爬数", "PS02", "短信验证码"),
    MESSAGE_CODE_VALIDATE_TIMEOUT("短信验证码校验超时", "PS", "爬数", "PS02", "短信验证码"),
    WAITING_USER_INPUT_IMAGE_CODE("等待用户输入图片验证码", "PS", "爬数", "PS03", "图片验证码"),
    IMAGE_CODE_VALIDATE_SUCCESS("图片验证码校验成功", "PS", "爬数", "PS03", "图片验证码"),
    IMAGE_CODE_VALIDATE_TIMEOUT("图片验证码校验超时", "PS", "爬数", "PS03", "图片验证码"),
    REFRESHING_IMAGE_CODE("刷新图片验证码", "PS", "爬数", "PS03", "图片验证码"),
    LOGIN_FAIL("登陆失败", "PS", "爬数", "PS04", "用户登陆"),
    LOGIN_SUCCESS("登陆成功", "PS", "爬数", "PS04", "用户登陆"),
    START_CRAWL("开始抓取", "PS", "爬数", "PS05", "数据抓取"),
    CRAWL_FAIL("抓取失败", "PS", "爬数", "PS05", "数据抓取"),
    CRAWL_SUCCESS("抓取成功", "PS", "爬数", "PS05", "数据抓取"),
    CRAWL_COMPLETE("爬数任务执行完成", "PS", "爬数", "PS05", "数据抓取"),
    CRAWL_INTERRUPT("抓取中断", "PS", "爬数", "PS05", "数据抓取"),
    USER_REFRESH_RETRY_CRAWL_INTERRUPT("用户刷新任务或者重试,抓取中断", "PS", "爬数", "PS06", "用户刷新"),
    DATA_PREPROCESSED_FAIL("数据预处理失败", "XS", "洗数", "XS01", "数据预处理"),
    DATA_PREPROCESSED_SUCCESS("数据预处理成功", "XS", "洗数", "XS01", "数据预处理"),
    DATA_PROCESS_FAIL("数据处理失败", "XS", "洗数", "XS02", "数据处理"),
    DATA_PROCESS_SUCCESS("数据处理成功", "XS", "洗数", "XS02", "数据处理"),
    DATA_CLEAN_SUCCESS("数据清洗成功", "XS", "洗数", "XS03", "数据清洗"),
    DATA_SAVE_FAIL("数据保存失败", "XS", "洗数", "XS04", "数据保存"),
    DATA_SAVE_SUCCESS("数据保存成功", "XS", "洗数", "XS04", "数据保存"),
    CALLBACK_FAIL("回调通知失败", "HD", "回调", "HD01", "回调通知"),
    CALLBACK_SUCCESS("回调通知成功", "HD", "回调", "HD01", "回调通知"),
    TASK_SUCCESS("任务成功", "WC", "完成", "WC01", "任务完成"),
    TASK_FAIL("任务失败", "WC", "完成", "WC01", "任务完成"),
    TASK_CANCEL("任务取消", "WC", "完成", "WC01", "任务完成"),
    TASK_TIMEOUT("任务超时", "WC", "完成", "WC01", "任务完成");


    private String text;//指令日志信息
    private String stageCode;//阶段编码
    private String stageText;//阶段信息
    private String stepCode;//环节编码
    private String stepText;//环节信息


    ETaskStep(String text, String stageCode, String stageText, String stepCode, String stepText) {
        this.text = text;
        this.stageCode = stageCode;
        this.stageText = stageText;
        this.stepCode = stepCode;
        this.stepText = stepText;
    }

    public String getText() {
        return text;
    }

    public String getStepCode() {
        return stepCode;
    }

    public String getStageCode() {
        return stageCode;
    }

    public String getStageText() {
        return stageText;
    }

    public static String getStepCodeByText(String text) {
        if (Objects.nonNull(text)) {
            for (ETaskStep item : ETaskStep.values()) {
                if (text.equals(item.getText())) {
                    return item.getStepCode();
                }
            }
        }
        return null;
    }

    public static String getStageCodeByStepCode(String stepCode) {
        if (Objects.nonNull(stepCode)) {
            for (ETaskStep item : ETaskStep.values()) {
                if (stepCode.equals(item.getStepCode())) {
                    return item.getStageCode();
                }
            }
        }
        return null;
    }

    public static String getStageTextByStepCode(String stepCode) {
        if (Objects.nonNull(stepCode)) {
            for (ETaskStep item : ETaskStep.values()) {
                if (stepCode.equals(item.getStepCode())) {
                    return item.getStageText();
                }
            }
        }
        return null;
    }


}
