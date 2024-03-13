package com.thoth.server.model.domain;

import com.thoth.server.controller.view.SecretView;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Secret extends SecuredResource {
    @Id
    private String name;
    private String value;
    private String salt;
    @Column(updatable = false)
    private Instant createdAt;
    public SecretView toView(String userSID, String organizationSID) {
        SecretView view = new SecretView();
        view.setName(name);
        view.setCreatedAt(createdAt);
        setView(view, userSID, organizationSID);
        return view;
    }
}
