package com.thoth.server.beans;

import com.thoth.server.configuration.security.token.ThothAuthenticationToken;
import com.thoth.server.model.domain.Permission;
import com.thoth.server.model.domain.SecuredResource;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;


public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public ThothAuthenticationToken getAuthentication() {
        return (ThothAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getUserSID() {
        return getAuthentication().getName();
    }

    @Override
    public String getOrganizationSID() {
        return getAuthentication().getOrganizationSID();

    }

    @Override
    public boolean canRead(Optional<? extends SecuredResource> securedResource) {
        return securedResource.map(this::canRead).orElse(true);
    }

    @Override
    public boolean canWrite(Optional<? extends SecuredResource> securedResource) {
        return securedResource.map(this::canWrite).orElse(true);
    }

    @Override
    public boolean canRead(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized {
        return securedResource.getCreatedBy().equals(getUserSID()) ||
                securedResource.getAllowedUserList().stream().anyMatch(p -> p.getSid().equals(getUserSID())) ||
                securedResource.getAllowedOrganizationList().stream().anyMatch(p -> p.getSid().equals(getOrganizationSID()));
    }

    @Override
    public boolean canWrite(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized {
        return securedResource.getCreatedBy().equals(getUserSID()) ||
                securedResource.getAllowedUserList().stream()
                        .filter(p -> p.getPermission() == Permission.W)
                        .anyMatch(p -> p.getSid().equals(getUserSID())) ||
                securedResource.getAllowedOrganizationList().stream()
                        .filter(p -> p.getPermission() == Permission.W)
                        .anyMatch(p -> p.getSid().equals(getOrganizationSID()));
    }

    @Override
    public <T extends SecuredResource> Specification<T> securedSpecification(Specification<T> spec, Class<T> cls) {
        return spec.and(spec
                .or((r, q, qb) -> {
                    q.distinct(true);
                    return qb.equal(r.get("createdBy"), getUserSID());
                })
                .or((r, q, cb) -> {
                    q.distinct(true);
                    return cb.equal(r.join("allowedUserList", JoinType.LEFT).get("sid"), getUserSID());
                })
                .or((r, q, cb) -> {
                    q.distinct(true);
                    return cb.equal(r.join("allowedOrganizationList", JoinType.LEFT).get("sid"), getOrganizationSID());
                }));
    }

    @Override
    public void fillSecuredResource(SecuredResource securedResource) {
        securedResource.setCreatedBy(getUserSID());
    }

}