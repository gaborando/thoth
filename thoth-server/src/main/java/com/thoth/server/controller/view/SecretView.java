package com.thoth.server.controller.view;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
public class SecretView extends SecuredResourceView{
    private String name;
    private Instant createdAt;

}
