package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.dto.moxie.*;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.processor.thirdparty.facade.fund.FundService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/9/15.
 */
@Service
public class MoxieBusinessService implements InitializingBean {
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
    @Autowired
    private TaskService taskService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String BASE_64_IMG = "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";
    private static String BASE_64_IMG_KEY_PREFIX = "test-base64-img-key:";


    /**
     * 本地缓存
     */
    private final LoadingCache<String, List<MoxieCityInfoVO>> cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(() -> {
                List<MoxieCityInfoVO> list = this.queryCityList();
                if (list == null) {
                    list = Lists.newArrayList();
                }
                logger.info("load local cache of moxie city list : list={}", JSON.toJSONString(list));
                return list;
            }));


    @Override
    public void afterPropertiesSet() throws Exception {
        List<MoxieCityInfoVO> cityList = this.queryCityList();
        logger.info("获取魔蝎城市公积金列表: cityList={}", JSON.toJSONString(cityList));
        if (CollectionUtils.isEmpty(cityList)) {
            return;
        }
        this.cache.put("citys", cityList);
    }

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param eventNoticeDTO
     */
    public void grabFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();
        String message = eventNoticeDTO.getMessage();
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
     * @param eventNoticeDTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void bill(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        String moxieTaskId = eventNoticeDTO.getMoxieTaskId();
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
        //获取魔蝎数据,调用洗数,传递账单数据
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
            //记录抓取日志
            taskLogService.insert(taskId, ETaskStep.CRAWL_SUCCESS.getText(), new Date(), null);
            taskLogService.insert(taskId, ETaskStep.CRAWL_COMPLETE.getText(), new Date(), null);
            logger.info("handle moxie business moxieResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, moxieResult);
        } catch (Exception e) {
            logger.error("handle moxie business error:bill fail", e);
            taskLogService.insert(taskId, ETaskStep.CRAWL_FAIL.getText(), new Date(), e.getMessage());
            throw new Exception("获取公积金信息失败");
        }
        try {
            String processResult = fundService.fund(taskId, moxieResult);
            //记录数据保存日志
            taskLogService.insert(taskId, ETaskStep.DATA_SAVE_SUCCESS.getText(), new Date(), null);
            logger.info("handle moxie business processResult,taskId={},moxieTaskId={},result={}", taskId, moxieTaskId, processResult);
            return processResult;
        } catch (Exception e) {
            logger.error("handle moxie business error:process fail", e);
            taskLogService.insert(taskId, ETaskStep.DATA_SAVE_FAIL.getText(), new Date(), e.getMessage());
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
//        JSONObject object = (JSONObject) fundMoxieService.queryTaskStatus(moxieTaskId);
//        if (!object.containsKey("phase_status")) {
//            return map;
//        }
        redisTemplate.opsForValue().setIfAbsent(BASE_64_IMG_KEY_PREFIX + taskId, "1");
//        if (StringUtils.equalsIgnoreCase(object.getString("phase_status"), "WAIT_CODE")) {
        String value1 = redisTemplate.opsForValue().get(BASE_64_IMG_KEY_PREFIX + taskId);
        if (Integer.valueOf(value1) <= 1) {
            if (true){
//            if (object.containsKey("input")) {
//                String type = object.getJSONObject("input").getString("type");
                String type = "img";
//                String value = object.getJSONObject("input").getString("value");
                String value = BASE_64_IMG;
//                Long waitSeconds = object.getJSONObject("input").getLong("wait_seconds");
                Long waitSeconds = 60L;


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
        redisTemplate.opsForValue().setIfAbsent(BASE_64_IMG_KEY_PREFIX + taskId, "2");
        return map;
    }

    /**
     * 获取魔蝎城市公积金列表
     *
     * @return
     */
    public List<MoxieCityInfoVO> getCityList() {

        List<MoxieCityInfoVO> list = Lists.newArrayList();
        try {
            list = cache.get("citys");
        } catch (ExecutionException e) {
            logger.error("获取魔蝎城市公积金列表缓存信息失败", e);
        }
        return list;

    }

    public void loginSuccess(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directiveDTO.setDirective(EMoxieDirective.LOGIN_SUCCESS.getText());
        moxieDirectiveService.process(directiveDTO);
    }

    public void loginFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieDirectiveDTO directiveDTO = new MoxieDirectiveDTO();
        directiveDTO.setMoxieTaskId(eventNoticeDTO.getMoxieTaskId());
        directiveDTO.setDirective(EMoxieDirective.LOGIN_FAIL.getText());
        directiveDTO.setRemark(eventNoticeDTO.getMessage());
        moxieDirectiveService.process(directiveDTO);
    }

    /**
     * 创建魔蝎任务,得到moxieTaskId
     *
     * @param taskId
     * @param params
     * @return
     */
    @Transactional
    public Map<String, Object> createMoxieTask(Long taskId, MoxieLoginParamsDTO params) {
        //1.轮询指令,已经提交过登录,获取魔蝎异步回调登录状态
        Map<String, Object> map = Maps.newHashMap();
        TaskAttribute attribute = taskAttributeService.findByName(taskId, "moxie-taskId", false);
        if (attribute != null && StringUtils.isNotBlank(attribute.getValue())) {
            logger.info("taskId={}已生成魔蝎任务id,执行查询指令", taskId);
            map = this.queryLoginStatusFromDirective(taskId);
            return map;
        }

        //2.提交登录,调用魔蝎接口创建魔蝎任务,如果任务创建失败,返回登录失败及异常信息
        String moxieId = null;
        try {
            moxieId = fundMoxieService.createTasks(params);
        } catch (RequestFailedException e) {
            map.put("directive", EMoxieDirective.LOGIN_FAIL.getText());
            String result = e.getResult();
            JSONObject object = (JSONObject) JsonUtils.toJsonObject(result);
            if (object.containsKey("detail")) {
                map.put("information", object.getString("detail"));
            } else {
                map.put("information", "登录失败,请重试");
            }
            return map;

        }
        //3.任务创建成功,写入task_attribute,开始轮询此接口,等待魔蝎回调登录状态信息
        taskAttributeService.insertOrUpdateSelective(taskId, "moxie-taskId", moxieId);
        //任务创建成功,记录登录account,登录失败重试会更新accountNo
        taskService.updateTask(taskId, params.getAccount(), null);
        map.put("directive", "waiting");
        map.put("information", "请等待");
        return map;
    }


    /**
     * 查询指令,获取公积金账户登录状态
     *
     * @param taskId
     * @return
     */
    private Map<String, Object> queryLoginStatusFromDirective(Long taskId) {
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
                //如果是登录成功或失败,删掉指令,下一个页面轮询/next_derictive的时候转为waiting
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

    /**
     * 拼装获取的城市列表信息为所需格式
     *
     * @return
     */
    private List<MoxieCityInfoVO> queryCityList() {
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
