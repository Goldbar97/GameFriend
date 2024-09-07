package com.gamefriend.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

  public String generateToken(String email) {

    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
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
    } catch (Exception e) {
    }

    return false;
  }

  public Authentication getAuthentication(String token) {

    Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

    UserDetails userDetails = User.builder()
        .username(claims.getSubject())
        .build();

    return new UsernamePasswordAuthenticationToken(userDetails, null, null);
  }
}