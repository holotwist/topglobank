package com.topglobanksoft.user_service.dto;

// import jakarta.validation.constraints.Email; // Email will come from token
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    // Email and password are no longer part of this DTO for direct creation.
    // Email will be taken from the JWT after Keycloak authentication.
    // Password is managed by Keycloak.

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber; // Optional

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address; // Optional
}