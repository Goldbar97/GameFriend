package com.gamefriend.component;

import com.gamefriend.exception.CustomException;
import com.gamefriend.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisComponent {

  private final RedisTemplate<String, Long> redisTemplate;
  private static final String SIGN_IN_FAIL_COUNT = "SignInFailCount:";
  private static final String ACCOUNT_LOCK = "AccountLock:";
  @Value("${spring.security.maximum-sign-in-attempt}")
  private int maxAttempt;
  @Value("${spring.security.lock-duration}")
  private int lockDuration;

  public void signInFailed(String key) {

    if (isLocked(key)) {
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
    Long value = redisTemplate.opsForValue().increment(SIGN_IN_FAIL_COUNT + key);

    if (value != null && value >= maxAttempt) {
      lock(key);
      redisTemplate.delete(SIGN_IN_FAIL_COUNT + key);
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
  }

  public void signInSuccess(String key) {

    if (!isLocked(key)) {
      redisTemplate.delete(SIGN_IN_FAIL_COUNT + key);
    } else {
      throw new CustomException(ErrorCode.ACCOUNT_LOCKED);
    }
  }

  public void lock(String key) {

    redisTemplate.opsForValue().set(ACCOUNT_LOCK + key, 1L, lockDuration, TimeUnit.MINUTES);
  }

  public boolean isLocked(String key) {

    return Boolean.TRUE.equals(redisTemplate.hasKey(ACCOUNT_LOCK + key));
  }
}