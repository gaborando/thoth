package com.thoth.server.beans;

import com.thoth.server.model.domain.SecuredResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface IAuthenticationFacade {
    Authentication getAuthentication();

    String getUserSID();

    Set<String> getUserOrgs();

    boolean canAccess(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized;
    boolean canAccess(Optional<? extends SecuredResource> securedResource);

    <T extends SecuredResource> Specification<T> securedSpecification(Specification<T> spec, Class<T> cls);

    void fillSecuredResource(SecuredResource securedResource);
}