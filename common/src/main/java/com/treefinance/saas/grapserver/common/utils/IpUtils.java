package com.treefinance.saas.grapserver.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luoyihua on 2017/5/2.
 */
public final class IpUtils {

    private static final Logger logger = LoggerFactory.getLogger(IpUtils.class);

    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.){3}[0-9]{1,3}");
    
    private static final String UNKNOWN = "unknown";

    private IpUtils() {}

    /**
     * 从请求头中提取请求来源IP
     *
     * @param request 请求对象 {@link HttpServletRequest}
     * @return IP Address
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");

        if (StringUtils.isNotBlank(ip)) {
            try {
                Matcher m = IP_PATTERN.matcher(ip);
                if (m.find()) {
                    ip = m.group();
                }
            } catch (Exception e) {
                logger.error("getIpAddress exception ", e);
            }
        }

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    /**
     * 获取服务器IP地址
     */
    public static String getServerIp() {
        String serverIp = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                ip = ni.getInetAddresses().nextElement();
                serverIp = ip.getHostAddress();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                        && !ip.getHostAddress().contains(":")) {
                    serverIp = ip.getHostAddress();
                    break;
                }
            }
        } catch (SocketException e) {
            logger.error("getServerIp exception ", e);
        }
        return serverIp;
    }
    
}
