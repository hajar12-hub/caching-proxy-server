package com.proxy.controller;

import com.proxy.proxy.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ProxyController {

    private final ProxyService proxyService;

    public ProxyController(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> handleRequest(HttpServletRequest request) {

        // extraire les infos de la requête
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String cacheKey = queryString != null ? path + "?" + queryString : path;

        // déléguer au service
        return proxyService.handleRequest(cacheKey, method);
    }
}