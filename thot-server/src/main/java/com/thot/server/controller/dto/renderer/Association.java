package com.thot.server.controller.dto.renderer;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Association {
    private String type;
    private String id;
    private String property;
}
