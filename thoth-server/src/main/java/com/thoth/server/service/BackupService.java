package com.thoth.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thoth.server.model.domain.*;
import com.thoth.server.model.domain.security.Permission;
import com.thoth.server.model.domain.security.SecuredResource;
import com.thoth.server.model.repository.BackupRepository;
import com.thoth.server.model.repository.DatasourcePropertiesRepository;
import com.thoth.server.model.repository.RendererRepository;
import com.thoth.server.model.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

@Service
public class BackupService {

    private final BackupRepository backupRepository;

    private final TemplateRepository templateRepository;
    private final DatasourcePropertiesRepository datasourcePropertiesRepository;
    private final RendererRepository rendererRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public BackupService(BackupRepository backupRepository, TemplateRepository templateRepository, DatasourcePropertiesRepository datasourcePropertiesRepository, RendererRepository rendererRepository) {
        this.backupRepository = backupRepository;
        this.templateRepository = templateRepository;
        this.datasourcePropertiesRepository = datasourcePropertiesRepository;
        this.rendererRepository = rendererRepository;
        mapper.registerModule(new JavaTimeModule());
    }

    public List<BackupListItem> getBackups(ResourceType resourceType, String resourceId, String uSid, String oSid) throws IllegalAccessException {
        var object = getResource(resourceType, resourceId);
        if(object instanceof SecuredResource sr){
            if(sr.checkPermission(uSid, oSid, false) == null){
                throw new IllegalAccessException("You do not have the permission to backup this resource");
            }
        }
        return  backupRepository.findAllByResource(resourceType, resourceId);
    }

    public Object getResource(ResourceType resourceType, String resourceId) {
        Object object = null;
        if(resourceType == ResourceType.TEMPLATE){
            object = templateRepository.findById(resourceId).orElseThrow();
        } else if (resourceType == ResourceType.DATASOURCE) {
            object = datasourcePropertiesRepository.findById(resourceId).orElseThrow();
        } else if (resourceType == ResourceType.RENDERER) {
            object = rendererRepository.findById(resourceId).orElseThrow();
        }
        return object;
    }

    public Backup backup(ResourceType resourceType, String resourceId, String uSid, String oSid) throws Exception {
        var b = new Backup();
        b.setIdentifier("bku_" + UUID.randomUUID());
        b.setResourceId(resourceId);
        b.setResourceType(resourceType);
        b.setTimestamp(Instant.now());

        var object = getResource(resourceType, resourceId);
        if(object instanceof SecuredResource sr){
            if(sr.checkPermission(uSid, oSid, false) != Permission.W){
                throw new IllegalAccessException("You do not have the permission to backup this resource");
            }
        }
        b.setData(mapper.writeValueAsString(object));

        return backupRepository.save(b);
    }

    public Backup findById(String backupId, String uSid, String oSid) throws IllegalAccessException {
        var b =  backupRepository.findById(backupId).orElseThrow();
        var object = getResource(b.getResourceType(), b.getResourceId());
        if(object instanceof SecuredResource sr){
            if(sr.checkPermission(uSid, oSid, false) == null){
                throw new IllegalAccessException("You do not have the permission to backup this resource");
            }
        }
        return b;
    }

    public void delete(String backupId, String userSID, String organizationSID) throws IllegalAccessException {
        backupRepository.delete(findById(backupId, userSID, organizationSID));
    }
}
