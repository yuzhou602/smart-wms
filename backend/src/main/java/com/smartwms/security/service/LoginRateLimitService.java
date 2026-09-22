package com.smartwms.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnBean(StringRedisTemplate.class)
public class LoginRateLimitService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCK_TIME_MINUTES = 15;
    private static final String LOGIN_ATTEMPT_KEY = "login:attempt:";
    private static final String LOGIN_LOCK_KEY = "login:lock:";

    public boolean isLocked(String username) {
        String lockKey = LOGIN_LOCK_KEY + username;
        return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
    }

    public void recordFailedAttempt(String username) {
        String attemptKey = LOGIN_ATTEMPT_KEY + username;
        Long attempts = redisTemplate.opsForValue().increment(attemptKey);
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        }
        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            lockAccount(username);
        }
    }

    public void lockAccount(String username) {
        String lockKey = LOGIN_LOCK_KEY + username;
        redisTemplate.opsForValue().set(lockKey, "locked", LOCK_TIME_MINUTES, TimeUnit.MINUTES);
        log.warn("账户 {} 因多次登录失败被锁定 {} 分钟", username, LOCK_TIME_MINUTES);
    }

    public void resetAttempts(String username) {
        String attemptKey = LOGIN_ATTEMPT_KEY + username;
        redisTemplate.delete(attemptKey);
    }

    public long getRemainingLockTime(String username) {
        String lockKey = LOGIN_LOCK_KEY + username;
        Long ttl = redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }
}
