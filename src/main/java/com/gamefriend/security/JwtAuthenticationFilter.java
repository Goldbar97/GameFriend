package com.gamefriend.security;

import com.gamefriend.component.JwtProvider;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final String TOKEN_HEADER = "Authorization";
  private final String TOKEN_PREFIX = "Bearer ";
  private final JwtProvider jwtProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveToken(request);

    if (token != null) {
      try {
        if (!jwtProvider.validateToken(token)) {
          throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } else {
          Authentication authentication = jwtProvider.getAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (CustomException e) {
        setErrorResponse(response, e);
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {

    String bearerToken = request.getHeader(TOKEN_HEADER);

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(TOKEN_PREFIX.length());
    }
    return null;
  }

  private void setErrorResponse(HttpServletResponse response, CustomException e)
      throws IOException {

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(e.getStatusCode());
    response.getWriter().write(e.getMessage());
  }
}