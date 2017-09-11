package com.treefinance.saas.grapserver.biz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.treefinance.commonservice.facade.location.*;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.grapserver.dao.entity.TaskDevice;
import com.treefinance.saas.grapserver.dao.mapper.TaskDeviceMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by luoyihua on 2017/5/2.
 */
@Service
public class TaskDeviceService {
    private static final Logger logger = LoggerFactory.getLogger(TaskDeviceService.class);

    @Autowired
    private TaskDeviceMapper taskDeviceMapper;
    private ThreadPoolExecutor executor;
    @Autowired
    private GeocodeService geocodeService;
    @Autowired
    private IpLocationService ipLocationService;
    private Map<String, String> geoCoordSysTypeMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        int processNumber = Runtime.getRuntime().availableProcessors();
        executor = new ThreadPoolExecutor(processNumber * 2, processNumber * 10, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue(processNumber * 50), new ThreadPoolExecutor.CallerRunsPolicy());
        geoCoordSysTypeMap.put("bd09ll", "BD_09_LL");
        geoCoordSysTypeMap.put("gcj02ll", "GCJ_02_LL");
        geoCoordSysTypeMap.put("wgs84ll", "WGS_84_LL");
    }

    public void create(String deviceInfo, String ipAddress, String coorType, Long taskId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("taskId={}, ipAddress={}, coorType={}, deviceInfo==>{}", taskId, ipAddress, coorType, deviceInfo);
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Map cardInfoMap = objectMapper.readValue(deviceInfo, Map.class);
                    TaskDevice record = new TaskDevice();
                    record.setId(UidGenerator.getId());
                    record.setTaskId(taskId);
                    // 增加经纬度校验
                    String positionData = (String) cardInfoMap.get("positionData");
                    if (StringUtils.isNotEmpty(positionData)){
                        List<String> positionDataList = Splitter.on(',').omitEmptyStrings().splitToList(positionData);
                        Double positionX = Doubles.tryParse(positionDataList.get(0));
                        Double positionY = Doubles.tryParse(positionDataList.get(1));
                        record.setPositionX(positionX);
                        record.setPositionY(positionY);
                        // 根据经纬度查询省市
                        if(positionX != null && positionY != null){
                            GeoPosition geoPosition = null;
                            String geoCoordSysType = geoCoordSysTypeMap.get(coorType);
                            if (StringUtils.isEmpty(geoCoordSysType)) {
                                geoPosition = geocodeService.findPosition(positionX, positionY);
                            } else {
                                geoPosition = geocodeService.findPosition(positionX, positionY, GeoCoordSysType.valueOf(geoCoordSysType));
                            }
                            // 填入省市名称
                            if (geoPosition != null){
                                record.setProvinceName(geoPosition.getProvince());
                                record.setCityName(geoPosition.getCity());
                            }
                        }
                    }
                    record.setAppVersion((String) cardInfoMap.get("appVersion"));
                    record.setPlatformId(cardInfoMap.get("platformId") == null ? null : Ints.tryParse(cardInfoMap.get("platformId").toString()) );
                    record.setPhoneBrand((String) cardInfoMap.get("phoneBrand"));
                    record.setPhoneModel((String) cardInfoMap.get("phoneModel"));
                    record.setPhoneVersion((String) cardInfoMap.get("phoneVersion"));
                    record.setOperatorName((String) cardInfoMap.get("operatorName"));
                    record.setNetModel((String) cardInfoMap.get("netModel"));
                    record.setIdfa((String) cardInfoMap.get("idfa"));
                    record.setOpenudid((String) cardInfoMap.get("openudid"));
                    record.setImei(cardInfoMap.get("imei") == null ? null : cardInfoMap.get("imei").toString());
                    record.setMacAddress((String) cardInfoMap.get("macAddress"));
                    record.setComment((String) cardInfoMap.get("comment"));
                    record.setOperatorCode(cardInfoMap.get("operatorCode") == null ? null : cardInfoMap.get("operatorCode").toString());
                    record.setCpuabi((String) cardInfoMap.get("cpuabi"));
                    record.setIsEmulator(objToBoolean(cardInfoMap.get("isEmulator")));
                    record.setIsJailbreak(objToBoolean(cardInfoMap.get("isJailbreak")));
                    record.setImsi(cardInfoMap.get("imsi") == null ? null : cardInfoMap.get("imsi").toString());
                    record.setIpAddress(ipAddress);
                    IpLocation ipLocation = ipLocationService.findLocation(ipAddress);
                    record.setIpPosition(ipLocation.getProvince());
                    taskDeviceMapper.insertSelective(record);
                } catch (GeoException ex) {
                    logger.error("经纬度解析地址异常={}", ex);
                } catch (IpLocateException ex) {
                    logger.error("ipAddress={}, Ip解析地址异常={}", ipAddress, ex);
                } catch (Exception ex) {
                    logger.error("设备信息处理失败={}", ex);
                }
            }
        });
    }

    private Boolean objToBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        return Boolean.parseBoolean(obj.toString());
    }
}
