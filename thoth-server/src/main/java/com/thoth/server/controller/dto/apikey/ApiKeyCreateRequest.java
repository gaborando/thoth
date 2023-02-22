package com.thoth.server.controller.dto.apikey;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiKeyCreateRequest {
    private String name;
    private Instant expiry;
}
