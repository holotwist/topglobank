package com.topglobanksoft.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//DTO class used to transfer data into the app
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String idUser; // Changed from Long (to use with Keycloak)
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private BigDecimal balance;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;
    private String roles;
}