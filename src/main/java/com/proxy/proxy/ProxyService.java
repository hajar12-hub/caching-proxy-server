package com.proxy.proxy;

import com.proxy.cache.CacheManager;
import com.proxy.config.AppConfig;
import com.proxy.model.CachedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class ProxyService {

    private final CacheManager cacheManager;
    private final AppConfig appConfig;
    private final RestTemplate restTemplate;

    public ResponseEntity<byte[]> handleRequest(String cacheKey, HttpMethod method) {

        // Vérifier le cache (seulement GET)
        if (method.equals(HttpMethod.GET) && cacheManager.contains(cacheKey)) {
            CachedResponse cached = cacheManager.get(cacheKey);
            HttpHeaders headers = new HttpHeaders();
            headers.addAll(cached.getHeaders());
            headers.add("X-Cache", "HIT");
            return new ResponseEntity<>(cached.getBody(), headers, cached.getStatus());
        }

        // Forwarder vers le serveur origin
        String targetUrl = appConfig.getOriginUrl() + cacheKey;
        ResponseEntity<byte[]> response = restTemplate.exchange(
                targetUrl,
                method,
                null,
                byte[].class
        );

        // Sauvegarder dans le cache (seulement GET + 2xx)
        if (method.equals(HttpMethod.GET) && response.getStatusCode().is2xxSuccessful()) {
            CachedResponse toCache = new CachedResponse(
                    response.getBody(),
                    response.getHeaders(),
                    (HttpStatus) response.getStatusCode()
            );
            cacheManager.save(cacheKey, toCache);
        }

        // Retourner la réponse avec X-Cache: MISS
        HttpHeaders headers = new HttpHeaders();
        headers.addAll(response.getHeaders());
        headers.add("X-Cache", "MISS");

        return new ResponseEntity<>(response.getBody(), headers, response.getStatusCode());
    }
}