package com.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProxyArgs {

    @Value("${port:}")
    private String port;

    @Value("${origin:}")
    private String origin;

    public String getPort() { return port; }
    public String getOrigin() { return origin; }
}
