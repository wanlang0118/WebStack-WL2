package webstack.common.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 * 处理反向代理场景下的真实客户端 IP 提取
 */
public class IpUtil {

    private IpUtil() {}

    /**
     * 获取客户端真实 IP
     * 优先级：X-Forwarded-For → X-Real-IP → getRemoteAddr
     *
     * @param request HttpServletRequest
     * @return 客户端 IP 字符串
     */
    public static String getClientIp(HttpServletRequest request) {
        // 1. X-Forwarded-For：经过多级代理格式为 "clientIp, proxy1, proxy2"，取第一个
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            return ip.split(",")[0].trim();
        }

        // 2. Nginx 常用的 X-Real-IP
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip.trim();
        }

        // 3. 兜底：直接从连接取
        return request.getRemoteAddr();
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    /**
     * IP 脱敏：IPv4 隐藏后两段，IPv6 仅保留前两段，其余以 * 代替，避免向前端泄露完整客户端 IP。
     *
     * @param ip 原始 IP
     * @return 脱敏后的 IP
     */
    public static String maskIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return ip;
        }
        if (ip.contains(".")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + ".*.*";
            }
        }
        if (ip.contains(":")) {
            String[] parts = ip.split(":");
            if (parts.length >= 2) {
                return parts[0] + ":" + parts[1] + ":****";
            }
        }
        return ip;
    }
}
