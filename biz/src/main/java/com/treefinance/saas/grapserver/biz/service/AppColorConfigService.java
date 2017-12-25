package com.treefinance.saas.grapserver.biz.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfigCriteria;
import com.treefinance.saas.grapserver.dao.mapper.AppColorConfigMapper;
import org.apache.commons.lang3.StringUtils;
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

    public AppColorConfig getByAppId(String appId, String style) {
        AppColorConfigCriteria example = new AppColorConfigCriteria();
        AppColorConfigCriteria.Criteria innerCriteria = example.createCriteria();
        innerCriteria.andAppIdEqualTo(appId).andStatusEqualTo((byte) 1);
        if (StringUtils.isNotBlank(style)) {
            innerCriteria.andStyleEqualTo(style);
        } else {
            innerCriteria.andStyleEqualTo("DEFAULT");
        }
        List<AppColorConfig> list = merchantColorConfigMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }
}
