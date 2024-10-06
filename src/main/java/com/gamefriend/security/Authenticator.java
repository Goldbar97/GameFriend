package com.gamefriend.security;

import com.gamefriend.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Authenticator {

  private final UserService userService;

  public Authentication getAuthentication(Claims claims) {

    String username = claims.getSubject();
    UserDetails userDetails = userService.loadUserByUsername(username);

    return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
        userDetails.getAuthorities());
  }
}