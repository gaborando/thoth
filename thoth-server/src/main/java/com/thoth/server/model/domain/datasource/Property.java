package com.thoth.server.model.domain.datasource;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Property implements Serializable {
    @NotBlank
    @Size(min = 1, max = 256)
    private String name;
    @NotBlank @Size(min = 1, max = 512)
    private String helper;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Property property = (Property) object;
        return Objects.equals(name, property.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
