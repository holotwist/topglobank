package com.topglobanksoft.user_service.controller;

import com.topglobanksoft.user_service.dto.UserCreateDTO;
import com.topglobanksoft.user_service.dto.UserDTO;
import com.topglobanksoft.user_service.dto.UserUpdateDTO;
import com.topglobanksoft.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // Helper to extract userId (Keycloak 'sub') from JWT
    private String getUserIdFromJwt(Jwt jwt) {
        String userId = jwt.getSubject(); // 'sub' claim is the Keycloak user ID (UUID)
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID ('sub' claim) not found in JWT");
        }
        return userId;
    }

    // Endpoint to provision user's application-specific profile after Keycloak authentication
    @PostMapping("/profile/provision")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')") // Requires user to be authenticated by Keycloak
    public ResponseEntity<UserDTO> provisionUserProfile(@Valid @RequestBody UserCreateDTO userCreateDTO,
                                                        @AuthenticationPrincipal Jwt jwt) {
        // JWT contains 'sub' (Keycloak ID), 'email', roles, etc.
        UserDTO newUserProfile = userService.provisionUserProfile(userCreateDTO, jwt);
        return new ResponseEntity<>(newUserProfile, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = getUserIdFromJwt(jwt);
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateMyProfile(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        String userId = getUserIdFromJwt(jwt);
        UserDTO updatedUser = userService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }


    // --- Admin Endpoints (RF-009) ---

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> listUsersAdmin() {
        List<UserDTO> users = userService.listAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserByIdAdmin(@PathVariable String id) { // Changed from Long
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserAdmin(@PathVariable String id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) { // Changed from Long
        UserDTO updatedUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserAdmin(@PathVariable String id) { // Changed from Long
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}