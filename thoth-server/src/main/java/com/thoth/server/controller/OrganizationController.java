package com.thoth.server.controller;

import com.thoth.server.beans.IAuthenticationFacade;
import com.thoth.server.controller.view.OrganizationView;
import com.thoth.server.model.domain.security.Organization;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.model.repository.OrganizationRepository;
import com.thoth.server.model.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for managing organizations
 */
@RestController
@RequestMapping("/organizations")
@Secured("ROLE_USER")
public class OrganizationController {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final IAuthenticationFacade authenticationFacade;

    public OrganizationController(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            IAuthenticationFacade authenticationFacade) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
    }

    /**
     * Get all organizations
     * 
     * @param page Page number
     * @return Page of organizations
     */
    @GetMapping("/")
    public ResponseEntity<Page<OrganizationView>> findAll(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok(organizationRepository.findAll(
                authenticationFacade.securedSpecification(Specification.allOf(), Organization.class),
                PageRequest.of(page, 30, Sort.by(Sort.Order.asc("name"))))
                .map(p -> p.toView(
                        authenticationFacade.getUserSID(),
                        authenticationFacade.getOrganizationSID()
                )));
    }

    /**
     * Get an organization by ID
     * 
     * @param id Organization ID
     * @return The organization if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationView> findById(
            @PathVariable Long id
    ) {
        Optional<Organization> organization = organizationRepository.findById(id);
        return organization.map(p -> ResponseEntity.ok(p.toView(
                        authenticationFacade.getUserSID(),
                        authenticationFacade.getOrganizationSID())))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new organization
     * 
     * @param organization The organization to create
     * @return The created organization
     */
    @PostMapping("/")
    @Secured("ROLE_ADMIN") // Only admins can create organizations
    public ResponseEntity<OrganizationView> create(
            @RequestBody Organization organization
    ) {
        // Set created by
        authenticationFacade.fillSecuredResource(organization);

        Organization savedOrganization = organizationRepository.save(organization);
        return ResponseEntity.ok(savedOrganization.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Update an organization
     * 
     * @param id Organization ID
     * @param organization The updated organization
     * @return The updated organization
     */
    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_ORGANIZATION_ADMIN"}) // Admins and org admins can update organizations
    public ResponseEntity<OrganizationView> update(
            @PathVariable Long id,
            @RequestBody Organization organization
    ) {
        Optional<Organization> existingOrganization = organizationRepository.findById(id);
        if (existingOrganization.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user has permission to update this organization
        Organization orgToUpdate = existingOrganization.get();
        if (!authenticationFacade.canWrite(orgToUpdate)) {
            return ResponseEntity.status(403).build();
        }

        // Update fields
        orgToUpdate.setName(organization.getName());
        orgToUpdate.setDescription(organization.getDescription());

        // Update permissions
        orgToUpdate.setAllowedUserList(organization.getAllowedUserList());
        orgToUpdate.setAllowedOrganizationList(organization.getAllowedOrganizationList());

        Organization updatedOrganization = organizationRepository.save(orgToUpdate);
        return ResponseEntity.ok(updatedOrganization.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Delete an organization
     * 
     * @param id Organization ID
     * @return No content if successful
     */
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN") // Only admins can delete organizations
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        if (organization.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if organization has users
        Organization orgToDelete = organization.get();
        if (!orgToDelete.getUsers().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        organizationRepository.delete(orgToDelete);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a user to an organization
     * 
     * @param organizationId Organization ID
     * @param userId User ID
     * @return The updated organization
     */
    @PostMapping("/{organizationId}/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_ORGANIZATION_ADMIN"}) // Admins and org admins can add users
    public ResponseEntity<OrganizationView> addUser(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        Optional<User> user = userRepository.findById(userId);

        if (organization.isEmpty() || user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user has permission to update this organization
        Organization orgToUpdate = organization.get();
        if (!authenticationFacade.canWrite(orgToUpdate)) {
            return ResponseEntity.status(403).build();
        }

        // Add user to organization
        User userToAdd = user.get();
        userToAdd.setOrganization(orgToUpdate);
        userRepository.save(userToAdd);

        return ResponseEntity.ok(orgToUpdate.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Remove a user from an organization
     * 
     * @param organizationId Organization ID
     * @param userId User ID
     * @return The updated organization
     */
    @DeleteMapping("/{organizationId}/users/{userId}")
    @Secured({"ROLE_ADMIN", "ROLE_ORGANIZATION_ADMIN"}) // Admins and org admins can remove users
    public ResponseEntity<OrganizationView> removeUser(
            @PathVariable Long organizationId,
            @PathVariable Long userId
    ) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        Optional<User> user = userRepository.findById(userId);

        if (organization.isEmpty() || user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user has permission to update this organization
        Organization orgToUpdate = organization.get();
        if (!authenticationFacade.canWrite(orgToUpdate)) {
            return ResponseEntity.status(403).build();
        }

        // Remove user from organization
        User userToRemove = user.get();
        if (userToRemove.getOrganization() != null && 
            userToRemove.getOrganization().getId().equals(organizationId)) {
            userToRemove.setOrganization(null);
            userRepository.save(userToRemove);
        }

        return ResponseEntity.ok(orgToUpdate.toView(
                authenticationFacade.getUserSID(),
                authenticationFacade.getOrganizationSID()));
    }

    /**
     * Get all users in an organization
     * 
     * @param organizationId Organization ID
     * @return List of users
     */
    @GetMapping("/{organizationId}/users")
    public ResponseEntity<List<User>> getUsers(
            @PathVariable Long organizationId
    ) {
        Optional<Organization> organization = organizationRepository.findById(organizationId);
        if (organization.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Check if user has permission to read this organization
        Organization org = organization.get();
        if (!authenticationFacade.canRead(org)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(org.getUsers());
    }
}
