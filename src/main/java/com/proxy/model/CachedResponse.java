package com.proxy.model;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Data
public class CachedResponse implements Serializable {
    private byte[] body;
    private HttpHeaders headers;
    private HttpStatus status;

    public CachedResponse(byte[] body, HttpHeaders headers, HttpStatus status) {
        this.body = body;
        this.headers = headers;
        this.status = status;
    }
}