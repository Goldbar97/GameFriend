package com.gamefriend.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class ClientUtils {

  public static String getIP(HttpServletRequest request) {

    String ip = request.getHeader("X-Forwarded-For");

    if (StringUtils.hasText(ip) && ip.contains(",")) {
      ip = ip.split(",")[0];
    }

    if (isNotValid(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    if (isNotValid(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (isNotValid(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP");
    }

    if (isNotValid(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (isNotValid(ip)) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  private static boolean isNotValid(String ip) {

    return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
  }
}