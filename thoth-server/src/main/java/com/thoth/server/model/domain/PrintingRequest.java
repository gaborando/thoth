package com.thoth.server.model.domain;

import com.thoth.server.controller.view.PrintingRequestView;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class PrintingRequest extends SecuredResource {

    @Id
    private String identifier;

    @ManyToOne
    private Client client;
    private String printService;
    @ManyToOne
    private Template template;
    @ManyToOne
    private Renderer renderer;

    private ZonedDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PrintingRequestStatus status;

    private ZonedDateTime dequeuedAt;
    private ZonedDateTime executedAt;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private int copies;
    @Column(columnDefinition = "TEXT")
    private String svg;

    public PrintingRequestView toView(String uSid, String oSid) {
        var v = new PrintingRequestView();
        v.setClient(client.toView(uSid, oSid));
        v.setIdentifier(identifier);
        v.setTemplate(template == null ? null: template.toListItemView(uSid, oSid));
        v.setRenderer(renderer == null ? null : renderer.toListItemView(uSid, oSid));

        v.setCreatedAt(createdAt);
        v.setCreatedBy(getCreatedBy() != null ? getCreatedBy().getUsername() : null);
        v.setStatus(status);
        v.setErrorMessage(errorMessage);
        v.setDequeuedAt(dequeuedAt);
        v.setExecutedAt(executedAt);
        v.setCopies(copies);
        v.setSvg(svg);
        v.setPrintService(printService);

        return v;
    }
}
