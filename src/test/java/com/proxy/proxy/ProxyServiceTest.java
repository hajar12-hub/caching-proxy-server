package com.proxy.proxy;

import com.proxy.cache.CacheManager;
import com.proxy.config.AppConfig;
import com.proxy.model.CachedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProxyServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private AppConfig appConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProxyService proxyService;

    // ✅ Test 1 — Cache HIT → ne doit pas appeler le serveur origin
    @Test
    void shouldReturnCachedResponse_whenCacheHit() {
        // Given
        String cacheKey = "/products";
        HttpHeaders cachedHeaders = new HttpHeaders();
        CachedResponse cached = new CachedResponse(
                "{\"products\": []}".getBytes(),
                cachedHeaders,
                HttpStatus.OK
        );
        when(cacheManager.contains(cacheKey)).thenReturn(true);
        when(cacheManager.get(cacheKey)).thenReturn(cached);

        // When
        ResponseEntity<byte[]> response = proxyService.handleRequest(cacheKey, HttpMethod.GET);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("HIT", response.getHeaders().getFirst("X-Cache"));
        verify(restTemplate, never()).exchange(any(), any(), any(), eq(byte[].class));
    }

    // ✅ Test 2 — Cache MISS → doit appeler le serveur origin et sauvegarder
    @Test
    void shouldForwardRequest_whenCacheMiss() {
        // Given
        String cacheKey = "/products";
        HttpHeaders responseHeaders = new HttpHeaders();
        ResponseEntity<byte[]> originResponse = new ResponseEntity<>(
                "{\"products\": []}".getBytes(),
                responseHeaders,
                HttpStatus.OK
        );
        when(cacheManager.contains(cacheKey)).thenReturn(false);
        when(appConfig.getOriginUrl()).thenReturn("http://dummyjson.com");
        when(restTemplate.exchange(
                "http://dummyjson.com/products",
                HttpMethod.GET,
                null,
                byte[].class
        )).thenReturn(originResponse);

        // When
        ResponseEntity<byte[]> response = proxyService.handleRequest(cacheKey, HttpMethod.GET);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MISS", response.getHeaders().getFirst("X-Cache"));
        verify(cacheManager, times(1)).save(eq(cacheKey), any(CachedResponse.class));
    }

    // ✅ Test 3 — POST → ne doit jamais utiliser le cache
    @Test
    void shouldNotUseCache_whenMethodIsPost() {
        // Given
        String cacheKey = "/products";
        HttpHeaders responseHeaders = new HttpHeaders();
        ResponseEntity<byte[]> originResponse = new ResponseEntity<>(
                "{}".getBytes(),
                responseHeaders,
                HttpStatus.OK
        );
        when(appConfig.getOriginUrl()).thenReturn("http://dummyjson.com");
        when(restTemplate.exchange(
                "http://dummyjson.com/products",
                HttpMethod.POST,
                null,
                byte[].class
        )).thenReturn(originResponse);

        // When
        proxyService.handleRequest(cacheKey, HttpMethod.POST);

        // Then
        verify(cacheManager, never()).contains(any());
        verify(cacheManager, never()).save(any(), any());
    }

    // ✅ Test 4 — Réponse 4xx → ne doit pas sauvegarder dans le cache
    @Test
    void shouldNotCache_whenResponseIsNotSuccessful() {
        // Given
        String cacheKey = "/unknown";
        HttpHeaders responseHeaders = new HttpHeaders();
        ResponseEntity<byte[]> originResponse = new ResponseEntity<>(
                "Not Found".getBytes(),
                responseHeaders,
                HttpStatus.NOT_FOUND
        );
        when(cacheManager.contains(cacheKey)).thenReturn(false);
        when(appConfig.getOriginUrl()).thenReturn("http://dummyjson.com");
        when(restTemplate.exchange(
                "http://dummyjson.com/unknown",
                HttpMethod.GET,
                null,
                byte[].class
        )).thenReturn(originResponse);

        // When
        ResponseEntity<byte[]> response = proxyService.handleRequest(cacheKey, HttpMethod.GET);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(cacheManager, never()).save(any(), any());
    }
}