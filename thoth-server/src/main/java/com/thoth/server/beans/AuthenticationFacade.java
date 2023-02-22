package com.thoth.server.beans;

import com.thoth.server.configuration.security.token.ThothAuthenticationToken;
import com.thoth.server.model.domain.SecuredResource;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public boolean canAccess(SecuredResource securedResource) {
        var orgs = getOrganizationSID();
        return securedResource.getCreatedBy().equals(getUserSID()) || securedResource.getAllowedUserList().contains(getUserSID()) || securedResource.getAllowedOrganizationList().stream().anyMatch(orgs::contains);
    }

    @Override
    public boolean canAccess(Optional<? extends SecuredResource> securedResource) {
        return securedResource.map(this::canAccess).orElse(true);
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
                    return cb.equal(r.join("allowedUserList", JoinType.LEFT), getUserSID());
                })
                .or((r, q, cb) -> {
                    q.distinct(true);
                    return cb.equal(r.join("allowedOrganizationList", JoinType.LEFT), getOrganizationSID());
                }));
    }

    @Override
    public void fillSecuredResource(SecuredResource securedResource) {
        securedResource.setCreatedBy(getUserSID());
    }

}