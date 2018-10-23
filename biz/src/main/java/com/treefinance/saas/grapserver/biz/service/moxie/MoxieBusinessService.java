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
import com.treefinance.saas.grapserver.biz.cache.RedisDao;
import com.treefinance.saas.grapserver.biz.service.TaskAttributeService;
import com.treefinance.saas.grapserver.biz.service.TaskLogService;
import com.treefinance.saas.grapserver.biz.service.TaskNextDirectiveService;
import com.treefinance.saas.grapserver.biz.service.TaskService;
import com.treefinance.saas.grapserver.common.enums.ETaskStatus;
import com.treefinance.saas.grapserver.common.enums.ETaskStep;
import com.treefinance.saas.grapserver.common.enums.moxie.EMoxieDirective;
import com.treefinance.saas.grapserver.common.exception.RequestFailedException;
import com.treefinance.saas.grapserver.common.model.dto.TaskDTO;
import com.treefinance.saas.grapserver.common.model.dto.moxie.*;
import com.treefinance.saas.grapserver.common.model.vo.moxie.MoxieCityInfoVO;
import com.treefinance.saas.grapserver.common.utils.DataConverterUtils;
import com.treefinance.saas.grapserver.common.utils.JsonUtils;
import com.treefinance.saas.grapserver.dao.entity.TaskAttribute;
import com.treefinance.saas.grapserver.dao.entity.TaskLog;
import com.treefinance.saas.grapserver.facade.enums.ETaskAttribute;
import com.treefinance.saas.taskcenter.facade.request.MoxieTaskEventNoticeRequest;
import com.treefinance.saas.taskcenter.facade.service.MoxieTaskEventNoticeFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private TaskAttributeService taskAttributeService;
    @Autowired
    private FundMoxieService fundMoxieService;
    @Autowired
    private TaskNextDirectiveService taskNextDirectiveService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private MoxieTimeoutService moxieTimeoutService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private GeocodeService geocodeService;
    @Autowired
    private MoxieTaskEventNoticeFacade moxieTaskEventNoticeFacade;

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
        MoxieTaskEventNoticeRequest request = DataConverterUtils.convert(eventNoticeDTO, MoxieTaskEventNoticeRequest.class);
        moxieTaskEventNoticeFacade.bill(request);
    }

    /**
     * 魔蝎任务是否需要验证码处理
     *
     * @param taskId
     * @return
     */
    private Map<String, Object> requireCaptcha(Long taskId) {
        Map<String, Object> map = Maps.newHashMap();
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
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
                //短信验证码需要自定义一个不同的value值,区分是两次不同的验证码请求,供前端轮询比较
                int inputCount = Optional.ofNullable(this.getVerifyCodeCount(taskId)).orElse(0);
                if (StringUtils.equalsIgnoreCase(type, "sms")) {//需要短信验证码
                    moxieCaptchaDTO.setValue(inputCount + "");
                    map.put("directive", "require_sms");
                    map.put("information", moxieCaptchaDTO);
                    logger.info("魔蝎公积金任务需要短信验证码,moxieCaptchaDTO={},taskId={}", JSON.toJSONString(moxieCaptchaDTO), taskId);
                    List<TaskLog> list = taskLogService.queryTaskLog(taskId, ETaskStep.WAITING_USER_INPUT_MESSAGE_CODE.getText());
                    if (list.size() <= inputCount) {
                        taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_MESSAGE_CODE.getText(), new Date(), null);
                    }
                }
                if (StringUtils.equalsIgnoreCase(type, "img")) {//需要图片验证码
                    map.put("directive", "require_picture");
                    map.put("information", moxieCaptchaDTO);
                    logger.info("魔蝎公积金任务需要图片验证码,moxieCaptchaDTO={},taskId={}", JSON.toJSONString(moxieCaptchaDTO), taskId);
                    List<TaskLog> list = taskLogService.queryTaskLog(taskId, ETaskStep.WAITING_USER_INPUT_IMAGE_CODE.getText());
                    if (list.size() <= inputCount) {
                        taskLogService.insert(taskId, ETaskStep.WAITING_USER_INPUT_IMAGE_CODE.getText(), new Date(), null);
                    }
                }
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
        TaskAttribute attribute = taskAttributeService.findByName(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), false);
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
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), moxieId);
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
                    //为了防止用户输入验证码过慢导致登录超时,若要输入验证码,则重置登录时间,重新计时90s
                    moxieTimeoutService.resetLoginTaskTimeOut(taskId);
                    return captchaMap;
                } else {
                    logger.info("taskId={}公积金登录轮询,查询验证码时,判断不需要验证码", taskId);
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
                        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.FUND_MOXIE_TASKID.getAttribute(), "");
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
        Map<String, List<MoxieCityInfoDTO>> map = Maps.newHashMap();
        for (MoxieCityInfoDTO moxieCityInfoDTO : list) {
            if (StringUtils.isBlank(moxieCityInfoDTO.getProvince())) {
                logger.error("魔蝎接口异常,存在null,moxieCityInfoDTO={}", JSON.toJSONString(moxieCityInfoDTO));
                continue;
            }
            List<MoxieCityInfoDTO> items = map.get(moxieCityInfoDTO.getProvince());
            if (CollectionUtils.isEmpty(items)) {
                items = Lists.newArrayList(moxieCityInfoDTO);
            } else {
                items.add(moxieCityInfoDTO);
            }
            map.put(moxieCityInfoDTO.getProvince(), items);
        }
//        Map<String, List<MoxieCityInfoDTO>> map = list.stream().collect(Collectors.groupingBy(MoxieCityInfoDTO::getProvince));
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
        logger.info("taskId={}公积金输入验证码moxieTaskId={},input={}", taskId, moxieTaskId, input);
        Integer count = this.incrVerifyCodeCount(taskId);
        logger.info("公积金taskId={}输入验证码次数+1,count={}", taskId, count);


    }

    @Transactional
    private Integer incrVerifyCodeCount(Long taskId) {
        int count = 1;
        TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.FUND_MOXIE_VERIFY_CODE_COUNT.getAttribute(), false);
        if (taskAttribute != null) {
            count = Integer.parseInt(taskAttribute.getValue()) + 1;
        }
        taskAttributeService.insertOrUpdateSelective(taskId, ETaskAttribute.FUND_MOXIE_VERIFY_CODE_COUNT.getAttribute(), count + "");
        String key = Joiner.on(":").useForNull("null").join(VERIFY_CODE_SMS_COUNT_PREFIX, taskId);
        redisDao.incrBy(key, 1, 5, TimeUnit.MINUTES);
        return count;
    }

    private Integer getVerifyCodeCount(Long taskId) {
        String key = Joiner.on(":").useForNull("null").join(VERIFY_CODE_SMS_COUNT_PREFIX, taskId);
        String value = redisDao.get(key);
        if (StringUtils.isNotBlank(value)) {
            return Integer.parseInt(value);
        } else {
            TaskAttribute taskAttribute = taskAttributeService.findByName(taskId, ETaskAttribute.FUND_MOXIE_VERIFY_CODE_COUNT.getAttribute(), false);
            if (taskAttribute == null) {
                return null;
            }
            value = taskAttribute.getValue();
            redisDao.setEx(key, value, 5, TimeUnit.MINUTES);
            return Integer.parseInt(value);
        }
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


    public void loginSuccess(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieTaskEventNoticeRequest request = DataConverterUtils.convert(eventNoticeDTO, MoxieTaskEventNoticeRequest.class);
        moxieTaskEventNoticeFacade.loginSuccess(request);
    }

    public void loginFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieTaskEventNoticeRequest request = DataConverterUtils.convert(eventNoticeDTO, MoxieTaskEventNoticeRequest.class);
        moxieTaskEventNoticeFacade.loginFail(request);
    }

    /**
     * 魔蝎任务采集失败业务处理
     *
     * @param eventNoticeDTO
     */
    public void grabFail(MoxieTaskEventNoticeDTO eventNoticeDTO) {
        MoxieTaskEventNoticeRequest request = DataConverterUtils.convert(eventNoticeDTO, MoxieTaskEventNoticeRequest.class);
        moxieTaskEventNoticeFacade.grabFail(request);
    }

}
