package com.thoth.server.configuration.security;

import lombok.Data;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Base64;

@Data
public class SecuredTimestamp {
    private Instant timestamp;
    private String secret;
}
