package com.thoth.server.controller.view;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Embeddable
public class AssociationView {
    @NotEmpty
    @Size(min = 3, max = 256)
    private String type;

    @Size(max = 256)
    private String id;
    @Size(max = 256)
    private String property;
}
