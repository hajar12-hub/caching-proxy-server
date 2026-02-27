package com.proxy.cache;

import com.proxy.model.CachedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


@Component
public class CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CachedResponse get(String cacheKey) {
        return (CachedResponse) redisTemplate.opsForValue().get(cacheKey);
    }

    public void save(String cacheKey, CachedResponse response) {
        redisTemplate.opsForValue().set(cacheKey, response);
    }

    public void clear() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    public boolean contains(String cacheKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
    }
}