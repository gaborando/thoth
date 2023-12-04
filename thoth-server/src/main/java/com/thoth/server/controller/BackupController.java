package com.thoth.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public List<BackupListItem> findAllByResource(@PathVariable ResourceType resourceType,@PathVariable String resourceId){
        return backupService.getBackups(resourceType, resourceId);
    }

    @PostMapping("/{resourceType}/{resourceId}")
    public Backup backup(@PathVariable ResourceType resourceType,@PathVariable String resourceId) throws JsonProcessingException {
        return  backupService.backup(resourceType, resourceId);
    }

    @GetMapping("/{backupId}")
    public Backup findById(@PathVariable String backupId){
        return backupService.findById(backupId);
    }

    @DeleteMapping("/{backupId}")
    public void deleteById(@PathVariable String backupId){
        backupService.delete(backupId);
    }
}
