package com.thoth.server.controller;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.view.OAuthProviderView;
import com.thoth.server.model.domain.security.OAuthProvider;
import com.thoth.server.model.repository.OAuthProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Controller for managing OAuth providers
 */
@RestController
@RequestMapping("/oauth-providers")
@Secured("ROLE_ADMIN") // Only admins can manage OAuth providers
public class OAuthProviderController {

    private final OAuthProviderRepository providerRepository;
    private final IAuthenticationFacade authenticationFacade;

    public OAuthProviderController(OAuthProviderRepository providerRepository, IAuthenticationFacade authenticationFacade) {
        this.providerRepository = providerRepository;
        this.authenticationFacade = authenticationFacade;
    }

    /**
     * Get all OAuth providers
     * 
     * @param page Page number
     * @return Page of OAuth providers
     */
    @GetMapping("/")
    public ResponseEntity<Page<OAuthProviderView>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(providerRepository.findAll(
                PageRequest.of(page, 30, Sort.by(Sort.Order.asc("name"))))
                .map(p -> p.toView(authenticationFacade.getUserSID(), authenticationFacade.getOrganizationSID())));
    }

    /**
     * Get an OAuth provider by ID
     * 
     * @param id Provider ID
     * @return The provider if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<OAuthProviderView> findById(
            @PathVariable Long id
    ) {
        Optional<OAuthProvider> provider = providerRepository.findById(id);
        return provider.map(p -> ResponseEntity.ok(p.toView(
                        authenticationFacade.getUserSID(),
                        authenticationFacade.getOrganizationSID())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new OAuth provider
     * 
     * @param provider The provider to create
     * @return The created provider
     */
    @PostMapping("/")
    public ResponseEntity<OAuthProviderView> create(
            @RequestBody OAuthProvider provider
    ) {
        // Set created by using the authentication facade
        authenticationFacade.fillSecuredResource(provider);

        OAuthProvider savedProvider = providerRepository.save(provider);
        return ResponseEntity.ok(savedProvider.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Update an OAuth provider
     * 
     * @param id Provider ID
     * @param provider The updated provider
     * @return The updated provider
     */
    @PutMapping("/{id}")
    public ResponseEntity<OAuthProviderView> update(
            @PathVariable Long id,
            @RequestBody OAuthProvider provider
    ) {
        Optional<OAuthProvider> existingProvider = providerRepository.findById(id);
        if (existingProvider.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OAuthProvider providerToUpdate = existingProvider.get();

        // Update fields
        providerToUpdate.setName(provider.getName());
        providerToUpdate.setClientId(provider.getClientId());
        providerToUpdate.setClientSecret(provider.getClientSecret());
        providerToUpdate.setAuthorizationUri(provider.getAuthorizationUri());
        providerToUpdate.setTokenUri(provider.getTokenUri());
        providerToUpdate.setUserInfoUri(provider.getUserInfoUri());
        providerToUpdate.setRedirectUri(provider.getRedirectUri());
        providerToUpdate.setScope(provider.getScope());
        providerToUpdate.setEnabled(provider.isEnabled());

        // Update permissions
        providerToUpdate.setAllowedUserList(provider.getAllowedUserList());
        providerToUpdate.setAllowedGroupList(provider.getAllowedGroupList());

        OAuthProvider updatedProvider = providerRepository.save(providerToUpdate);
        return ResponseEntity.ok(updatedProvider.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Delete an OAuth provider
     * 
     * @param id Provider ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<OAuthProvider> provider = providerRepository.findById(id);
        if (provider.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        providerRepository.delete(provider.get());
        return ResponseEntity.noContent().build();
    }
}
