package com.gamefriend.component;

import com.gamefriend.security.CustomUserDetails;
import com.gamefriend.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

  @Value("${jwt.secret.key}")
  private String key;
  @Value("${jwt.secret.expiration}")
  private long expirationTime;
  private SecretKey secretKey;

  @PostConstruct
  public void init() {

    secretKey = Keys.hmacShaKeyFor(key.getBytes());
  }

  public String generateToken(String email, UserRole role) {

    Claims claims = Jwts.claims().setSubject(email);
    claims.put("role", role.name());

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(secretKey)
        .compact();
  }

  public boolean validateToken(String token) {

    try {
      Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(token);

      return true;
    } catch (ExpiredJwtException e) {
      log.info("The token is expired");
    } catch (Exception e) {
      log.info("Exception is occurred in validateToken");
    }

    return false;
  }

  public Authentication getAuthentication(String token) {

    log.info("Starting getAuthentication");

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    String email = claims.getSubject();
    String role = claims.get("role", String.class);
    List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(role));

    UserDetails userDetails = new CustomUserDetails(email, roles);

    return new UsernamePasswordAuthenticationToken(userDetails, null, roles);
  }
}