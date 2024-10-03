package com.gamefriend.security;

import com.gamefriend.utils.XSSUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

  public XSSRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name) {
    String value = super.getParameter(name);
    return cleanXSS(value);
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] values = super.getParameterValues(name);
    if (values == null) {
      return null;
    }

    int length = values.length;
    String[] sanitizedValues = new String[length];
    for (int i = 0; i < length; i++) {
      sanitizedValues[i] = cleanXSS(values[i]);
    }
    return sanitizedValues;
  }

  private String cleanXSS(String value) {
    if (value != null) {
      // HTML 엔티티로 이스케이프
      value = XSSUtils.sanitize(value);
    }
    return value;
  }
}