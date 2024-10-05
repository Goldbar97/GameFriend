package com.gamefriend.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamefriend.dto.UserDTO;
import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisComponent {

  private static final String SIGN_IN_FAIL_COUNT = "SignInFailCount:";
  private static final String ACCOUNT_LOCK = "AccountLock:";
  private static final String USER_INFO = "UserInfo:";
  private final RedisTemplate<String, Long> stringLongRedisTemplate;
  private final StringRedisTemplate stringRedisTemplate;
  private final ObjectMapper objectMapper;
  @Value("${spring.security.maximum-sign-in-attempt}")
  private int maxAttempt;
  @Value("${spring.security.lock-duration}")
  private int lockDuration;
  @Value("${jwt.secret.expiration}")
  private int jwtDuration;

  public void signInFailed(String key) {

    if (isLocked(key)) {
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
    Long value = stringLongRedisTemplate.opsForValue().increment(SIGN_IN_FAIL_COUNT + key);

    if (value != null && value >= maxAttempt) {
      lock(key);
      stringLongRedisTemplate.delete(SIGN_IN_FAIL_COUNT + key);
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
  }

  public void signInSuccess(String key) {

    if (!isLocked(key)) {
      stringLongRedisTemplate.delete(SIGN_IN_FAIL_COUNT + key);
    } else {
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
  }

  public void lock(String key) {

    stringLongRedisTemplate.opsForValue()
        .set(ACCOUNT_LOCK + key, 1L, lockDuration, TimeUnit.MINUTES);
  }

  public boolean isLocked(String key) {

    return Boolean.TRUE.equals(stringLongRedisTemplate.hasKey(ACCOUNT_LOCK + key));
  }

  public void saveUserDTO(String username, UserDTO userDTO) {

    try {
      String JsonUserDTO = objectMapper.writeValueAsString(userDTO);
      stringRedisTemplate.opsForValue()
          .set(USER_INFO + username, JsonUserDTO, jwtDuration, TimeUnit.MILLISECONDS);
    } catch (JsonProcessingException e) {
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  public UserDTO getUserDTO(String username) {

    try {
      String JsonUserDTO = stringRedisTemplate.opsForValue().get(USER_INFO + username);
      if (JsonUserDTO != null) {
        return objectMapper.readValue(JsonUserDTO, UserDTO.class);
      }
      return null;
    } catch (Exception e) {
      throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }
}