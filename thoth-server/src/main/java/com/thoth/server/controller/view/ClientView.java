package com.thoth.server.controller.view;


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
