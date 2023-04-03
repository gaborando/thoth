package com.thoth.server.beans;

import com.thoth.server.configuration.security.token.ThothAuthenticationToken;
import com.thoth.server.model.domain.SecuredResource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

public interface IAuthenticationFacade {
    ThothAuthenticationToken getAuthentication();

    String getUserSID();

    String getOrganizationSID();

    boolean canRead(Optional<? extends SecuredResource> securedResource);
    boolean canWrite(Optional<? extends SecuredResource> securedResource);

    boolean canRead(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized;
    boolean canWrite(SecuredResource securedResource) throws HttpClientErrorException.Unauthorized;

    <T extends SecuredResource> Specification<T> securedSpecification(Specification<T> spec, Class<T> cls);

    void fillSecuredResource(SecuredResource securedResource);
}