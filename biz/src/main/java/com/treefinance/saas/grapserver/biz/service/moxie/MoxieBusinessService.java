package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCaptchaDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCityInfoDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.processor.thirdparty.facade.fund.FundService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieBusinessService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private MoxieDirectiveService moxieDirectiveService;
    @Autowired
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundMoxieService fundMoxieService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private FundService fundService;

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param moxieTaskId
     * @param message
     */
    public void grabFail(String moxieTaskId, String message) {
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue("moxie-taskId", moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();
        //1.记录采集失败日志
        taskLogService.insert(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), message);
        //2.发送任务失败指令
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
        Map<String, Object> map = Maps.newHashMap();
        map.put("taskErrorMsg", "爬数失败");
        directiveDTO.setRemark(JsonUtils.toJsonString(map));
        directiveDTO.setTaskId(taskId);
        directiveDTO.setMoxieTaskId(moxieTaskId);
        moxieDirectiveService.process(directiveDTO);

    }

    /**
     * 魔蝎账单通知业务处理
     *
     * @param moxieTaskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void bill(String moxieTaskId) {
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue("moxie-taskId", moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();
        //1.记录采集成功日志
        taskLogService.insert(taskId, ETaskStep.CRAWL_SUCCESS.getText(), new Date(), null);
        taskLogService.insert(taskId, ETaskStep.CRAWL_COMPLETE.getText(), new Date(), null);
        //2.获取魔蝎数据,调用洗数,传递账单数据
        Boolean result = true;
        String message = null;
        String processResult = null;
        try {
            processResult = this.billAndProcess(taskId, moxieTaskId);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill and process fail.taskId={},moxieTaskId={}", taskId, moxieTaskId, e);
            result = false;
            message = e.getMessage();
        }
        //3.根据洗数返回结果,发送任务成功或失败指令
        if (result) {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_SUCCESS.getText());
            directiveDTO.setRemark(processResult);
            moxieDirectiveService.process(directiveDTO);
        } else {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
            Map<String, Object> map = Maps.newHashMap();
            map.put("taskErrorMsg", message);
            directiveDTO.setRemark(JsonUtils.toJsonString(map));
            moxieDirectiveService.process(directiveDTO);
        }
    }

    private String billAndProcess(Long taskId, String moxieTaskId) throws Exception {
        String moxieResult = null;
        try {
            moxieResult = fundMoxieService.queryFunds(moxieTaskId);
            logger.info("handle moxie business moxieResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, moxieResult);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill fail", e);
            throw new Exception("获取公积金信息失败");
        }
        try {
            String processResult = fundService.fund(taskId, moxieResult);
            logger.info("handle moxie business processResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, processResult);
            return processResult;
        } catch (Exception e) {
            logger.error("handle moxie business error:process fail", e);
            throw new Exception("洗数失败");
        }
    }

    /**
     * 魔蝎任务是否需要验证码处理
     *
     * @param taskId
     * @return
     */
    public Map<String, Object> requireCaptcha(Long taskId) {
        Map<String, Object> map = Maps.newHashMap();
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, "moxie-taskId", false);
        if (taskAttribute == null) {
            logger.error("handle moxie business error : taskId={} doesn't have moxieTaskId matched in task_attribute", taskId);
            return map;
        }
        String moxieTaskId = taskAttribute.getValue();
        JSONObject object = (JSONObject) fundMoxieService.queryTaskStatus(moxieTaskId);
        if (!object.containsKey("phase_status")) {
            return map;
        }
        if (StringUtils.equalsIgnoreCase(object.getString("phase_status"), "WAIT_CODE")) {
            if (object.containsKey("input")) {
                String type = object.getJSONObject("input").getString("type");
                String value = object.getJSONObject("input").getString("value");
                Long waitSeconds = object.getJSONObject("input").getLong("wait_seconds");

                MoxieCaptchaDTO moxieCaptchaDTO = new MoxieCaptchaDTO();
                moxieCaptchaDTO.setType(type);
                moxieCaptchaDTO.setValue(value);
                moxieCaptchaDTO.setWaitSeconds(waitSeconds);
                if (StringUtils.equalsIgnoreCase(type, "sms")) {//需要短信验证码
                    map.put("directive", "require_sms");
                    map.put("information", moxieCaptchaDTO);
                    taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_IMAGE_CODE.getText(), new Date(), null);
                }
                if (StringUtils.equalsIgnoreCase(type, "img")) {//需要图片验证码
                    map.put("directive", "require_picture");
                    map.put("information", moxieCaptchaDTO);
                    taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_MESSAGE_CODE.getText(), new Date(), null);

                }

            }

        }
        return map;
    }

    /**
     * 拼装获取的城市列表信息为所需格式
     *
     * @return
     */
    public Object queryCityList() {
        List<MoxieCityInfoVO> result = Lists.newArrayList();
        List<MoxieCityInfoDTO> list = fundMoxieService.queryCityListEx();
        //<province,list>
        Map<String, List<MoxieCityInfoDTO>> map = list.stream().collect(Collectors.groupingBy(MoxieCityInfoDTO::getProvince));
        for (Map.Entry<String, List<MoxieCityInfoDTO>> entry : map.entrySet()) {
            MoxieCityInfoVO vo = new MoxieCityInfoVO();
            vo.setLabel(entry.getKey());
            vo.setSpell(convertToPinyinString(entry.getKey()));
            List<MoxieCityInfoVO> sonList = Lists.newArrayList();
            List<MoxieCityInfoDTO> dtoList = entry.getValue();
            for (MoxieCityInfoDTO dto : dtoList) {
                MoxieCityInfoVO sonVO = new MoxieCityInfoVO();
                sonVO.setLabel(dto.getCity_name());
                sonVO.setValue(dto.getArea_code());
                sonVO.setSpell(convertToPinyinString(dto.getCity_name()));
                sonVO.setStatus(dto.getStatus());
                sonList.add(sonVO);
            }
            sonList = sonList.stream().sorted((o1, o2) -> o1.getSpell().compareTo(o2.getSpell())).collect(Collectors.toList());
            vo.setList(sonList);
            result.add(vo);
        }
        result = result.stream().sorted((o1, o2) -> o1.getSpell().compareTo(o2.getSpell())).collect(Collectors.toList());
        return result;
    }

    private String convertToPinyinString(String name) {
        if (StringUtils.isBlank(name)) {
            return "";
        }
        String result = "";
        try {
            result = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
        } catch (PinyinException e) {
            logger.error("convert to pinyinString is error name={}", name, e);
        }
        return result;
    }

    /**
     * 查询指令,获取公积金账户登录状态
     *
     * @param taskId
     * @return
     */
    public Map<String, Object> queryLoginStatusFromDirective(Long taskId) {
        Map<String, Object> map = Maps.newHashMap();
        String content = taskNextDirectiveService.getNextDirective(taskId);
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断任务是否超时
//            if (taskServiceImpl.isTaskTimeout(taskid)) {
//                // 异步处理任务超时
//                taskTimeService.handleTaskTimeout(taskid);
//            }
            map.put("directive", "waiting");
            map.put("information", "请等待");
        } else {
            MoxieDirectiveDTO directiveMessage = JSON.parseObject(content, MoxieDirectiveDTO.class);
            EMoxieDirective directive = EMoxieDirective.directiveOf(directiveMessage.getDirective());
            if (directive == null) {
                map.put("directive", "waiting");
                map.put("information", "请等待");
            }
            if (directive.getStepCode() == 1) {
                map.put("directive", directiveMessage.getDirective());
                map.put("information", directiveMessage.getRemark());
                //如果是登录成功或失败,删掉指令,下一个页面轮询的时候转为waiting
                taskNextDirectiveService.deleteNextDirective(taskId, directiveMessage.getDirective());
                if (StringUtils.equals(directiveMessage.getDirective(), EMoxieDirective.LOGIN_FAIL.getText())) {
                    // 登录失败(如用户名密码错误),需删除task_attribute中此taskId对应的moxieTaskId,重新登录时,可正常轮询/login/submit接口
                    taskAttributeService.insertOrUpdateSelective(taskId, "moxie-taskId", "");
                }
            }
            if (directive.getStepCode() > 1) {
                map.put("directive", EMoxieDirective.LOGIN_SUCCESS.getText());
                map.put("information", "");
            }
        }
        return map;
    }

    public void loginSuccess(String moxieTaskId) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(moxieTaskId);
        directiveDTO.setDirective(EMoxieDirective.LOGIN_SUCCESS.getText());
        moxieDirectiveService.process(directiveDTO);
    }

    public void loginFail(String moxieTaskId, String message) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(moxieTaskId);
        directiveDTO.setDirective(EMoxieDirective.LOGIN_FAIL.getText());
        directiveDTO.setRemark(message);//登录错误信息
        moxieDirectiveService.process(directiveDTO);
    }
}
