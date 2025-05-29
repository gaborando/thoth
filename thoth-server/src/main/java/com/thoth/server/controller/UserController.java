package com.thoth.server.controller;

import com.thoth.server.beans.AuthenticationFacade;
import com.thoth.server.controller.view.UserView;
import com.thoth.server.model.domain.security.User;
import com.thoth.server.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Secured("ROLE_USER")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<Page<UserView>> findAll(
            @RequestParam(defaultValue = "0") int page,
            AuthenticationFacade facade
    ) {
        return ResponseEntity.ok(userService.search(Specification.allOf(),
                PageRequest.of(page, 30, Sort.by(Sort.Order.desc("createdAt")))).map(u -> u.toView(
                        facade.getUserSID(),
                        facade.getOrganizationSID()
                )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserView> findById(
            @PathVariable Long id,
            AuthenticationFacade facade
    ) {
        Optional<User> user = userService.findById(id);
        return user.map(u -> ResponseEntity.ok(u.toView(
                        facade.getUserSID(),
                        facade.getOrganizationSID())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserView> findByUsername(
            @PathVariable String username,
            AuthenticationFacade facade
    ) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(u -> ResponseEntity.ok(u.toView(
                        facade.getUserSID(),
                        facade.getOrganizationSID())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserView> update(
            @PathVariable Long id,
            @RequestBody User user,
            AuthenticationFacade facade
    ) {
        Optional<User> existingUser = userService.findById(id);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User updatedUser = userService.update(existingUser.get(), user);
        return ResponseEntity.ok(updatedUser.toView(
                facade.getUserSID(),
                facade.getOrganizationSID()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        userService.delete(user.get());
        return ResponseEntity.ok().build();
    }
}