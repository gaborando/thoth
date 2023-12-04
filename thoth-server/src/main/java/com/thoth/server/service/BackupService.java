package com.thoth.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.shaded.gson.Gson;
import com.thoth.server.model.domain.Backup;
import com.thoth.server.model.domain.BackupListItem;
import com.thoth.server.model.domain.ResourceType;
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

    public List<BackupListItem> getBackups(ResourceType resourceType, String resourceId){
        return  backupRepository.findAllByResource(resourceType, resourceId);
    }

    public Backup backup(ResourceType resourceType, String resourceId) throws JsonProcessingException {
        var b = new Backup();
        b.setIdentifier("bku_" + UUID.randomUUID());
        b.setResourceId(resourceId);
        b.setResourceType(resourceType);
        b.setTimestamp(Instant.now());
        if(resourceType == ResourceType.TEMPLATE){
            b.setData(mapper.writeValueAsString(templateRepository.findById(resourceId).orElseThrow()));
        } else if (resourceType == ResourceType.DATASOURCE) {
            b.setData(mapper.writeValueAsString(datasourcePropertiesRepository.findById(resourceId).orElseThrow()));
        } else if (resourceType == ResourceType.RENDERER) {
            b.setData(mapper.writeValueAsString(rendererRepository.findById(resourceId).orElseThrow()));
        }
        return backupRepository.save(b);
    }

    public Backup findById(String backupId) {
        return backupRepository.findById(backupId).orElseThrow();
    }

    public void delete(String backupId) {
        backupRepository.deleteById(backupId);
    }
}
