package com.thoth.server.model.domain;

import com.thoth.common.dto.ClientQueueMode;
import com.thoth.server.controller.view.ClientView;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @Enumerated(EnumType.STRING)
    private ClientQueueMode queueMode;

    public ClientView toView(String uSid, String oSid){
        ClientView view = new ClientView();
        view.setIdentifier(identifier);
        view.setName(name);
        view.setCreatedAt(createdAt);
        view.setPrintServices(printServices);
        view.setQueueMode(queueMode);
        setView(view, uSid, oSid);
        return view;
    }

}
