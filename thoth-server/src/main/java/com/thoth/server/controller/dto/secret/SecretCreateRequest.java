package com.thoth.server.controller.dto.secret;

import lombok.Data;

@Data
public class SecretCreateRequest {
    private String name;
    private String value;
}
