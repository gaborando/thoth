package com.thoth.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.model.domain.Backup;
import com.thoth.server.model.domain.BackupListItem;
import com.thoth.server.model.domain.ResourceType;
import com.thoth.server.service.BackupService;
import lombok.Getter;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backup")
@Secured("ROLE_USER")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/{resourceType}/{resourceId}")
    public List<BackupListItem> findAllByResource(@PathVariable ResourceType resourceType,@PathVariable String resourceId,
                                                  AuthenticationFacade facade) throws IllegalAccessException {
        return backupService.getBackups(resourceType, resourceId, facade.getUserSID(), facade.getOrganizationSID());
    }

    @PostMapping("/{resourceType}/{resourceId}")
    public Backup backup(@PathVariable ResourceType resourceType, @PathVariable String resourceId,
                         AuthenticationFacade facade) throws Exception {
        return  backupService.backup(resourceType, resourceId, facade.getUserSID(), facade.getOrganizationSID());
    }

    @GetMapping("/{backupId}")
    public Backup findById(@PathVariable String backupId,
                           AuthenticationFacade facade) throws IllegalAccessException {
        return backupService.findById(backupId, facade.getUserSID(), facade.getOrganizationSID());
    }

    @DeleteMapping("/{backupId}")
    public void deleteById(@PathVariable String backupId,
                           AuthenticationFacade facade) throws IllegalAccessException {
        backupService.delete(backupId, facade.getUserSID(), facade.getOrganizationSID());
    }
}
