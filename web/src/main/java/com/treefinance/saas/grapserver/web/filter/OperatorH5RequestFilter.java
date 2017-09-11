package com.treefinance.saas.grapserver.web.filter;

import com.treefinance.saas.grapserver.biz.config.SystemConfig;
import com.treefinance.saas.grapserver.dao.entity.AppLicense;
import com.treefinance.saas.grapserver.common.exception.ForbiddenException;
import com.treefinance.saas.grapserver.common.model.AppLicenseKey;
import com.treefinance.saas.grapserver.web.auth.AppLicenseManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 * Created by yh-treefinance on 2017/8/1.
 */
public class OperatorH5RequestFilter extends BaseRequestFilter {
    /**
     * 授权协议查询
     */
    private AppLicenseManager licenseManager;

    @Override
    protected void initFilterBean(FilterConfig filterConfig) throws ServletException {
        WebApplicationContext webApplicationContext =
                WebApplicationContextUtils.findWebApplicationContext(filterConfig.getServletContext());

        if (webApplicationContext == null) {
            throw new ServletException("Web application context failed to init...");
        }
        AppLicenseManager licenseManager = webApplicationContext.getBean(AppLicenseManager.class);
        if (licenseManager == null) {
            throw new ServletException("Can't find the instance of the class 'AppLicenseService'");
        }
        this.licenseManager = licenseManager;
        super.initFilterBean(filterConfig);
    }

    @Override
    RequestResolver getResolver(FilterConfig filterConfig) throws ServletException {
        try {
            WebApplicationContext webApplicationContext =
                    WebApplicationContextUtils.findWebApplicationContext(filterConfig.getServletContext());
            if (webApplicationContext == null) {
                throw new ServletException("Web application context failed to init...");
            }
            String target = filterConfig.getInitParameter("decrypt.target");
            SystemConfig config = webApplicationContext.getBean(SystemConfig.class);
            String h5GlobalAppId = config.getH5GlobalAppId();
            AppLicenseKey h5GlobalAppLicenseKey = getGlobalApp(h5GlobalAppId);
            return new OperatorH5RequestDecryptResolver(target, h5GlobalAppLicenseKey, config);
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

    /**
     * 获取H5用配置APP
     *
     * @param appId
     * @return
     */
    public AppLicenseKey getGlobalApp(String appId) throws ForbiddenException {
        AppLicense license = licenseManager.getAppLicense(appId);
        return new AppLicenseKey(license.getAppId(), license.getSdkPublicKey(), license.getSdkPrivateKey());
    }

}
