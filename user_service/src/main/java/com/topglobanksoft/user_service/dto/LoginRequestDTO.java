package com.topglobanksoft.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}