package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.TaskStepEnum;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCaptchaDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieCityInfoDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.MoxieDirectiveDTO;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param moxieTaskId
     * @param message
     */
    public void grabFail(String moxieTaskId, String message) {
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie grab business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue("moxie-taskId", moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie grab business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();
        //1.记录采集失败日志
        taskLogService.insert(taskId, TaskStepEnum.CRAWL_FAIL.getText(), new Date(), message);
        //2.发送任务失败指令
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
        directiveDTO.setRemark(message);
        directiveDTO.setTaskId(taskId);
        directiveDTO.setMoxieTaskId(moxieTaskId);
        moxieDirectiveService.process(directiveDTO);

    }

    /**
     * 魔蝎账单通知业务处理
     *
     * @param moxieTaskId
     */
    public void bill(String moxieTaskId) {
        if (StringUtils.isBlank(moxieTaskId)) {
            logger.error("handle moxie grab business error: moxieTaskId={} is null", moxieTaskId);
            return;
        }
        TaskAttribute taskAttribute = taskAttributeService.findByNameAndValue("moxie-taskId", moxieTaskId, false);
        if (taskAttribute == null) {
            logger.error("handle moxie grab business error: moxieTaskId={} doesn't have taskId matched in task_attribute", moxieTaskId);
            return;
        }
        long taskId = taskAttribute.getTaskId();
        //1.记录采集成功日志
        taskLogService.insert(taskId, TaskStepEnum.CRAWL_SUCCESS.getText(), new Date(), null);
        taskLogService.insert(taskId, TaskStepEnum.CRAWL_COMPLETE.getText(), new Date(), null);
        //2.调用洗数,传递账单数据
        // TODO: 2017/9/15  需要调用洗数的相关接口
        logger.info("调用洗数处理...");
        boolean result = false;
        String message = null;
        //3.根据洗数返回结果,发送任务成功或失败指令
        if (result) {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_SUCCESS.getText());
            moxieDirectiveService.process(directiveDTO);
        } else {
            MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
            directiveDTO.setMoxieTaskId(moxieTaskId);
            directiveDTO.setTaskId(taskId);
            directiveDTO.setDirective(EMoxieDirective.TASK_FAIL.getText());
            directiveDTO.setRemark(message);
            moxieDirectiveService.process(directiveDTO);
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
                }
                if (StringUtils.equalsIgnoreCase(type, "img")) {//需要图片验证码
                    map.put("directive", "require_picture");
                    map.put("information", moxieCaptchaDTO);
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
}
