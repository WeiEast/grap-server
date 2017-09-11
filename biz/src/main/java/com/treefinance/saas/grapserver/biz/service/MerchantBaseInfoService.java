package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfo;
import com.treefinance.saas.grapserver.dao.entity.MerchantBaseInfoCriteria;
import com.treefinance.saas.grapserver.dao.mapper.MerchantBaseInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/13.
 */
@Service
public class MerchantBaseInfoService {
    @Autowired
    private MerchantBaseInfoMapper merchantBaseInfoMapper;

    /**
     * 根据appId获取商户信息
     *
     * @param appId
     * @return
     */
    public MerchantBaseInfo getMerchantBaseInfoByAppId(String appId) {
        MerchantBaseInfoCriteria criteria = new MerchantBaseInfoCriteria();
        criteria.createCriteria().andAppIdEqualTo(appId);
        List<MerchantBaseInfo> list = merchantBaseInfoMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    /**
     * 获取所有商户列表
     *
     * @return
     */
    public List<MerchantBaseInfo> getAllMerchant() {
        MerchantBaseInfoCriteria criteria = new MerchantBaseInfoCriteria();
        criteria.createCriteria();
        return merchantBaseInfoMapper.selectByExample(criteria);
    }
}
