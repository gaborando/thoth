package com.thoth.server.beans;

import com.thoth.server.configuration.security.token.ThothAuthenticationToken;
import com.thoth.server.model.domain.security.Permission;
import com.thoth.server.model.domain.security.SecuredResource;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.model.repository.UserRepository;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;


public class AuthenticationFacade implements IAuthenticationFacade {

    private final UserRepository userRepository;

    public AuthenticationFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
    public boolean isOrganizationAdmin() {
        ThothAuthenticationToken auth = getAuthentication();
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ORGANIZATION_ADMIN"));
    }

    @Override
    public Long getUserOrganizationId() {
        // This would typically be retrieved from the user's profile or the authentication token
        // For now, we'll return null or parse it from the organization SID if it's a numeric value
        String orgSid = getOrganizationSID();
        if (orgSid != null && orgSid.matches("\\d+")) {
            return Long.parseLong(orgSid);
        }
        return null;
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
        return securedResource.checkPermission(getUserSID(), getOrganizationSID(), isOrganizationAdmin()) != null;
    }

    @Override
    public boolean canWrite(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized {
        return securedResource.checkPermission(getUserSID(), getOrganizationSID(), isOrganizationAdmin()) == Permission.W;
    }

    @Override
    public <T extends SecuredResource> Specification<T> securedSpecification(Specification<T> spec, Class<T> cls) {
        // If user is an organization admin, they can see all resources in their organization
        if (isOrganizationAdmin() && getUserOrganizationId() != null) {
            return spec.and(((Specification<T>) (r, q, qb) -> {
                q.distinct(true);
                Join<T, User> createdByJoin = r.join("createdBy", JoinType.LEFT);
                return qb.or(
                    qb.equal(createdByJoin.get("username"), getUserSID()),
                    qb.equal(r.get("organizationId"), getUserOrganizationId())
                );
            }));
        }

        // Otherwise, use the standard permission checks
        return spec.and(((Specification<T>) (r, q, qb) -> {
            q.distinct(true);
            Join<T, User> createdByJoin = r.join("createdBy", JoinType.LEFT);
            return qb.equal(createdByJoin.get("username"), getUserSID());
        }).or((r, q, cb) -> {
                    q.distinct(true);
                    return cb.equal(r.joinList("allowedUserList", JoinType.LEFT).get("sid"), getUserSID());
                })
                .or((r, q, cb) -> {
                    q.distinct(true);
                    return cb.equal(r.joinList("allowedOrganizationList", JoinType.LEFT).get("sid"), getOrganizationSID());
                })
                .or((r, q, cb) -> {
                    q.distinct(true);
                    // Include resources that belong to the user's organization
                    Long orgId = getUserOrganizationId();
                    if (orgId != null) {
                        return cb.equal(r.get("organizationId"), orgId);
                    }
                    return cb.disjunction(); // Empty predicate (always false)
                }));


    }

    @Override
    public void fillSecuredResource(SecuredResource securedResource) {
        User user = userRepository.findByUsername(getUserSID())
                .orElseThrow(() -> new RuntimeException("User not found: " + getUserSID()));
        securedResource.setCreatedBy(user);
        securedResource.setOrganizationId(getUserOrganizationId());
    }

}
