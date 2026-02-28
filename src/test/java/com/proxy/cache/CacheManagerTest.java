package com.proxy.cache;

import com.proxy.cache.CacheManager;
import com.proxy.model.CachedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheManagerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private CacheManager cacheManager;

    //  Test 1  contains() retourne true si la clé existe
    @Test
    void shouldReturnTrue_whenKeyExists() {
        when(redisTemplate.hasKey("/products")).thenReturn(true);
        assertTrue(cacheManager.contains("/products"));
    }

    //  Test 2  contains() retourne false si la clé n'existe pas
    @Test
    void shouldReturnFalse_whenKeyDoesNotExist() {
        when(redisTemplate.hasKey("/products")).thenReturn(false);
        assertFalse(cacheManager.contains("/products"));
    }

    //  Test 3  save() sauvegarde la réponse dans Redis
    @Test
    void shouldSaveResponseInRedis() {
        CachedResponse response = new CachedResponse(
                "{}".getBytes(),
                new HttpHeaders(),
                HttpStatus.OK
        );
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        cacheManager.save("/products", response);

        verify(valueOperations, times(1)).set("/products", response);
    }

    //  Test 4  get() retourne la réponse depuis Redis
    @Test
    void shouldReturnCachedResponse_whenKeyExists() {
        CachedResponse response = new CachedResponse(
                "{}".getBytes(),
                new HttpHeaders(),
                HttpStatus.OK
        );
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("/products")).thenReturn(response);

        CachedResponse result = cacheManager.get("/products");

        assertEquals(response, result);
    }

    //  Test 5  get() retourne null si la clé n'existe pas
    @Test
    void shouldReturnNull_whenKeyDoesNotExist() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("/unknown")).thenReturn(null);

        CachedResponse result = cacheManager.get("/unknown");

        assertNull(result);
    }

    //  Test 6  clear() vide tout le cache
    @Test
    void shouldClearAllCache() {
        var connection = mock(org.springframework.data.redis.connection.RedisConnection.class);
        var factory = mock(org.springframework.data.redis.connection.RedisConnectionFactory.class);

        when(redisTemplate.getConnectionFactory()).thenReturn(factory);
        when(factory.getConnection()).thenReturn(connection);

        cacheManager.clear();

        verify(connection, times(1)).flushAll();
    }
}
