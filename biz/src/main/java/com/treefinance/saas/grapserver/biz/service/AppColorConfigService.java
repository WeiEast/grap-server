package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfigCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppColorConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by luoyihua on 2017/5/2.
 */
@Service
public class AppColorConfigService {

    @Autowired
    private AppColorConfigMapper merchantColorConfigMapper;

    public AppColorConfig getByAppId(String appId) {
        AppColorConfigCriteria example = new AppColorConfigCriteria();
        example.createCriteria().andAppIdEqualTo(appId).andStatusEqualTo((byte) 1);
        List<AppColorConfig> list = merchantColorConfigMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
