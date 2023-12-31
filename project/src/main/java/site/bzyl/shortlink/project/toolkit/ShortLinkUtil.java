package site.bzyl.shortlink.project.toolkit;

import cn.hutool.core.date.LocalDateTimeUtil;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MILLIS;
import static site.bzyl.shortlink.project.common.constants.ShortLinkConstant.DEFAULT_PERMANENT_LINK_EXPIRE_TIME;

public class ShortLinkUtil {
    public static long getLinkExpireTimeMillis(LocalDateTime expireDateTime) {
        return Optional
                .ofNullable(expireDateTime)
                .map(each -> LocalDateTimeUtil.between(LocalDateTime.now(), each, MILLIS))
                .orElse(DEFAULT_PERMANENT_LINK_EXPIRE_TIME);
    }

    // 获取客户端的 IP 地址
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // 如果使用代理，X-Forwarded-For的值会是一串IP地址，取第一个有效IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }

        return ipAddress;
    }

    public static String getOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("windows")) {
            return "Windows";
        } else if (userAgent.toLowerCase().contains("mac")) {
            return "Mac OS";
        } else if (userAgent.toLowerCase().contains("linux")) {
            return "Linux";
        } else if (userAgent.toLowerCase().contains("android")) {
            return "Android";
        } else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
            return "iOS";
        } else {
            return "Unknown";
        }
    }

    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("edg")) {
            return "Microsoft Edge";
        } else if (userAgent.toLowerCase().contains("chrome")) {
            return "Google Chrome";
        } else if (userAgent.toLowerCase().contains("firefox")) {
            return "Mozilla Firefox";
        } else if (userAgent.toLowerCase().contains("safari")) {
            return "Apple Safari";
        } else if (userAgent.toLowerCase().contains("opera")) {
            return "Opera";
        } else if (userAgent.toLowerCase().contains("msie") || userAgent.toLowerCase().contains("trident")) {
            return "Internet Explorer";
        } else {
            return "Unknown";
        }
    }

    public static String getDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent.toLowerCase().contains("mobile")) {
            return "Mobile";
        }
        return "PC";
    }

    public static String getNetwork(HttpServletRequest request) {
        String actualIp = getClientIp(request);
        // 这里简单判断IP地址范围，您可能需要更复杂的逻辑
        // 例如，通过调用IP地址库或调用第三方服务来判断网络类型
        return actualIp.startsWith("192.168.") || actualIp.startsWith("10.") ? "WIFI" : "Mobile";
    }
}
