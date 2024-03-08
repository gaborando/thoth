package com.thoth.server.controller.view;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ClientView extends SecuredResourceView {
    private String identifier;


    private String name;

    private Instant createdAt;

    private List<String> printServices;
}
