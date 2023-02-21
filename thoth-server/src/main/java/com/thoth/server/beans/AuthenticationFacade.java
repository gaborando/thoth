package com.thoth.server.beans;

import com.nimbusds.jwt.JWTClaimsSet;
import com.thoth.server.model.domain.SecuredResource;
import com.thoth.server.model.domain.Template;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;


public class AuthenticationFacade implements IAuthenticationFacade {

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getUserSID() {
        return getAuthentication().getName();
    }

    @Override
    public Set<String> getUserOrgs() {
        return new HashSet<>((Collection<String>) ((JWTClaimsSet) getAuthentication().getCredentials()).getClaim("cognito:groups"));
    }

    @Override
    public boolean canAccess(SecuredResource securedResource) {
        var orgs = getUserOrgs();
        return securedResource.getCreatedBy().equals(getUserSID()) || securedResource.getAllowedUserList().contains(getUserSID()) || securedResource.getAllowedGroupList().stream().anyMatch(orgs::contains);
    }

    @Override
    public boolean canAccess(Optional<? extends SecuredResource> securedResource) {
        return securedResource.map(this::canAccess).orElse(true);
    }

    @Override
    public <T extends SecuredResource> Specification<T> securedSpecification(Specification<T> spec, Class<T> cls) {
        spec.and(spec.or(
                (r,q,qb)-> {
                    return qb.equal(r.get("createdBy"), getUserSID());
                }
        ));
        return null;
    }

    @Override
    public void fillSecuredResource(SecuredResource securedResource) {
        securedResource.setCreatedBy(getUserSID());
        securedResource.setAllowedGroupList(getUserOrgs());
    }

    /*
    @Override
    public Specification<? extends SecuredResource> securedSpecification(Specification<SecuredResource> spec) {
        return spec.and(spec.or(
                new Specification<? extends SecuredResource>() {
                    @Override
                    public Predicate toPredicate(Root<? extends SecuredResource> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                        return null;
                    }
                }
        ));
    }

    /*
    @Override
    public Specification<SecuredResource> securedSpecification(Specification<? extends SecuredResource> spec) {
        return spec.and(spec.or(
                (Specification<? extends SecuredResource>) (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), getUserSID())
        ));
    }*/
}