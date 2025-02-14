package com.thoth.server.controller.view;

import com.thoth.server.model.domain.Client;
import com.thoth.server.model.domain.PrintingRequestStatus;
import com.thoth.server.model.domain.Renderer;
import com.thoth.server.model.domain.Template;
import com.thoth.server.model.domain.security.SecuredResource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
public class PrintingRequestView extends SecuredResourceView {


    private String identifier;

    private ClientView client;
    private String printService;

    private TemplateListItemView template;

    private RendererListItemView renderer;

    private ZonedDateTime createdAt;
    private String createdBy;

    private PrintingRequestStatus status;
    private String errorMessage;
    private ZonedDateTime dequeuedAt;
    private ZonedDateTime executedAt;

    private int copies;
    private String svg;
}
