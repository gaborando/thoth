package com.thoth.server.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Client extends SecuredResource {

    @Id
    @NotBlank
    @Size(min = 3, max = 256)
    private String identifier;

    
    @NotBlank
    @Size(min = 3, max = 256)
    private String name;

    @Column(updatable = false)
    private Instant createdAt;

    @ElementCollection
    @NotEmpty
    private List<@NotBlank @Size(min = 3, max = 256)String> printServices;

}
