package com.treefinance.saas.grapserver.biz.service.moxie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.facade.location.GeoCoordSysType;
import com.treefinance.commonservice.facade.location.GeoPosition;
import com.treefinance.commonservice.facade.location.GeocodeService;
import com.treefinance.saas.grapserver.biz.config.DiamondConfig;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.biz.service.moxie.directive.MoxieDirectiveService;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.*;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.processor.thirdparty.facade.fund.FundService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
    private static String VERIFY_CODE_SMS_COUNT_PREFIX = "saas-grap-fund-verify-code-count";

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
    private DiamondConfig diamondConfig;
    @Autowired
    private MoxieTimeoutService moxieTimeoutService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private GeocodeService geocodeService;

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
            this.cache.put("citys", Lists.newArrayList());
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
        //任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = isTaskDone(taskId);
        if (flag) {
            return;
        }
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
     * 任务是否已经结束(即:任务是否已经为成功,失败或取消)
     *
     * @param taskId
     * @return
     */
    private boolean isTaskDone(long taskId) {
        TaskDTO task = taskService.getById(taskId);
        if (task == null) {
            logger.error("taskId={}不存在", taskId);
            return true;
        }
        Byte taskStatus = task.getStatus();
        if (ETaskStatus.CANCEL.getStatus().equals(taskStatus)
                || ETaskStatus.SUCCESS.getStatus().equals(taskStatus)
                || ETaskStatus.FAIL.getStatus().equals(taskStatus)) {
            logger.info("taskId={}任务已经结束,魔蝎后续重试回调不再处理", taskId);
            return true;
        }
        return false;
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

        //任务已经完成,不再继续后续处理.(当任务超时时,会发生魔蝎回调接口重试)
        boolean flag = isTaskDone(taskId);
        if (flag) {
            return;
        }

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
            moxieResult = fundMoxieService.queryFundsEx(moxieTaskId);
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
    private Map<String, Object> requireCaptcha(Long taskId) {
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
                    //短信验证码需要自定义一个不同的value值,区分是两次不同的验证码请求,供前端轮询比较
                    String key = Joiner.on(":").useForNull("null").join(VERIFY_CODE_SMS_COUNT_PREFIX, taskId);
                    moxieCaptchaDTO.setValue(redisTemplate.opsForValue().get(key));
                    map.put("directive", "require_sms");
                    map.put("information", moxieCaptchaDTO);
                    taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_MESSAGE_CODE.getText(), new Date(), null);
                }
                if (StringUtils.equalsIgnoreCase(type, "img")) {//需要图片验证码
                    map.put("directive", "require_picture");
                    map.put("information", moxieCaptchaDTO);
                    taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_IMAGE_CODE.getText(), new Date(), null);

                }
                logger.info("魔蝎公积金任务需要验证码,moxieCaptchaDTO={},taskId={}", JSON.toJSONString(moxieCaptchaDTO), taskId);
            }

        }
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
            logger.info("taskId={}已生成魔蝎任务moxieTaskId={},执行查询指令(验证登录是否超时,判断登录是否需要验证码,等待回调登录成功失败状态),等待魔蝎异步回调登录状态...",
                    taskId, attribute.getValue());
            map = this.queryLoginStatusFromDirective(taskId, attribute.getValue());
            return map;
        }

        //2.提交登录,调用魔蝎接口创建魔蝎任务,如果任务创建失败,返回登录失败及异常信息
        String moxieId = null;
        try {
            moxieId = fundMoxieService.createTasks(params);
            logger.info("taskId={}创建魔蝎任务成功moxieTaskId={},params={}", moxieId, JSON.toJSONString(params));
        } catch (RequestFailedException e) {
            logger.info("taskId={}创建魔蝎任务失败,params={},e={}", JSON.toJSONString(params), e);
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
        //3.魔蝎任务创建成功,写入task_attribute,开始轮询此接口,等待魔蝎回调登录状态信息
        taskAttributeService.insertOrUpdateSelective(taskId, "moxie-taskId", moxieId);
        //魔蝎任务创建成功,记录登录account,登录失败重试会更新accountNo
        taskService.updateTask(taskId, params.getAccount(), null);
        //魔蝎任务创建成功,记录任务创建时间,查询登录状态时判断登录是否超时.
        moxieTimeoutService.logLoginTime(taskId);
        map.put("directive", "waiting");
        map.put("information", "请等待");
        return map;
    }


    /**
     * 查询指令,获取公积金账户登录状态
     *
     * @param taskId
     * @param moxieTaskId
     * @return
     */
    private Map<String, Object> queryLoginStatusFromDirective(Long taskId, String moxieTaskId) {
        Map<String, Object> map = Maps.newHashMap();
        String content = taskNextDirectiveService.getNextDirective(taskId);
        if (StringUtils.isEmpty(content)) {
            // 轮询过程中，判断登录是否超时
            if (moxieTimeoutService.isLoginTaskTimeout(taskId)) {
                map.put("directive", EMoxieDirective.LOGIN_FAIL.getText());
                map.put("information", "登录超时,请重试");
                moxieTimeoutService.handleLoginTimeout(taskId, moxieTaskId);
            } else {
                //如果还未收到登录状态的指令,判断是否需要验证码
                Map<String, Object> captchaMap = this.requireCaptcha(taskId);
                if (!MapUtils.isEmpty(captchaMap)) {
                    return captchaMap;
                } else {
                    logger.info("taskId={}公积金登录轮询,查询验证码时,判断不需要验证码");
                    map.put("directive", "waiting");
                    map.put("information", "请等待");
                }
            }
        } else {
            MoxieDirectiveDTO directiveMessage = JSON.parseObject(content, MoxieDirectiveDTO.class);
            EMoxieDirective directive = EMoxieDirective.directiveOf(directiveMessage.getDirective());
            if (directive == null) {
                map.put("directive", "waiting");
                map.put("information", "请等待");
            } else {
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


    public void verifyCodeInput(Long taskId, String moxieTaskId, String input) {
        fundMoxieService.submitTaskInput(moxieTaskId, input);
        logger.info("taskId={}公积金输入验证码moxieTaskId={},input={}", moxieTaskId, input);
        String key = Joiner.on(":").useForNull("null").join(VERIFY_CODE_SMS_COUNT_PREFIX, taskId);
        Long count = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.opsForValue().getOperations().expire(key, 5, TimeUnit.MINUTES);
        logger.info("公积金taskId={}输入验证码次数+1,count={}", taskId, count);


    }

    public Object getCurrentProvince(Double latitude, Double longitude) {
        Map<String, Object> map = Maps.newHashMap();
        GeoPosition result = null;
        try {
            result = geocodeService.findPosition(latitude, longitude, GeoCoordSysType.BD_09_LL);
        } catch (Exception e) {
            logger.error("公积金调用公共服务定位当前省份失败,latitude={},longitude={}", latitude, longitude, e);
        }
        if (result == null) {
            return map;
        }
        String provinceName = result.getProvince();
        List<MoxieCityInfoVO> list = this.getCityList();
        List<MoxieCityInfoVO> currentList = list.stream()
                .filter(moxieCityInfoVO -> provinceName.contains(moxieCityInfoVO.getLabel()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(currentList)) {
            return map;
        }
        map.put("province", currentList.get(0).getLabel());
        map.put("list", currentList.get(0).getList());
        return map;
    }
}
