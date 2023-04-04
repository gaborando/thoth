package com.thoth.server.service;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.model.domain.ApiKey;
import com.thoth.server.model.repository.ApiKeyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    private final IAuthenticationFacade facade;

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    public ApiKeyService(ApiKeyRepository apiKeyRepository, IAuthenticationFacade facade) {
        this.apiKeyRepository = apiKeyRepository;
        this.facade = facade;
    }

    public ApiKey create(String name,
                         String userSID,
                         String organizationSID,
                         Instant expiry) {
        Assert.isTrue(
                userSID.startsWith("api_usid_") &&
                        userSID.matches("[a-z0-9_]+"),
                "error.organization.sid.not.valid");
        Assert.isTrue(
                organizationSID.startsWith("api_osid_") &&
                        organizationSID.matches("[a-z0-9_]+"),
                "error.organization.sid.not.valid");
        var token = new ApiKey();
        token.setName(name);
        token.setId("tkn_" + UUID.randomUUID());
        token.setCreatedAt(Instant.now());
        token.setExpiry(expiry);
        token.setUserSID(userSID);
        token.setOrganizationSID(organizationSID);
        token.setCreatedBy(facade.getUserSID());

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        token.setApiKey(base64Encoder.encodeToString(randomBytes));
        return apiKeyRepository.save(token);
    }

    @PreAuthorize("#apiKey.createdBy == @authenticationFacade.userSID")
    public void delete(ApiKey apiKey) {
        apiKeyRepository.delete(apiKey);
    }

    public Page<ApiKey> search(Specification<ApiKey> specification, PageRequest pageable) {
        var s = specification.and((r, q, cb) -> cb.equal(r.get("createdBy"), facade.getUserSID()));
        return apiKeyRepository.findAll(s, pageable);
    }

    public ApiKey checkKey(String apiKey) {
        var key = apiKeyRepository.findApiKeyByApiKey(apiKey).orElseThrow(() -> new AccessDeniedException("Invalid token"));
        if (key.getExpiry() != null && key.getExpiry().isBefore(Instant.now())) {
            throw new AccessDeniedException("Token Expired");
        }
        return key;
    }

    public void delete(String identifier) {
        delete(apiKeyRepository.findById(identifier).orElseThrow());
    }
}
