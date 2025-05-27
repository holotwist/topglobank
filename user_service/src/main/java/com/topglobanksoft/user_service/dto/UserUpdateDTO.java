package com.topglobanksoft.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    @Email(message = "Must be a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email; // Caution: Email changes might require re-verification.

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Address must be less than 255 characters")
    private String address;

    // Password updates should be handled via a separate, more secure endpoint/DTO.
    // Role updates are typically admin-only operations.
}