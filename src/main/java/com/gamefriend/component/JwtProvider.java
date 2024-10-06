package com.gamefriend.component;

import com.gamefriend.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
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

  public Claims claim(String token) {

    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}