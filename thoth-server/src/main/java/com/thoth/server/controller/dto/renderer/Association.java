package com.thoth.server.controller.dto.renderer;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Embeddable
public class Association {
    @NotEmpty
    @Size(min = 3, max = 256)
    private String type;

    @Size(max = 256)
    private String id;
    @Size(max = 256)
    private String property;
}
