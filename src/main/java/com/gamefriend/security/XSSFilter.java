package com.gamefriend.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class XSSFilter implements Filter {

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    if ("application/json".equalsIgnoreCase(servletRequest.getContentType())) {
      HttpServletRequest request = (HttpServletRequest) servletRequest;
      XSSRequestWrapper xssRequestWrapper = new XSSRequestWrapper(request);
      filterChain.doFilter(xssRequestWrapper, servletResponse);
    } else {
      filterChain.doFilter(servletRequest, servletResponse);
    }
  }
}