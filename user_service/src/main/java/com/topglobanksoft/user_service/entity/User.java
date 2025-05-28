package com.topglobanksoft.user_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//Map a table in the DataBase and establishes a unique restriction
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    //Defines the attributes and behaviours for a JPA entity to a user in the DataBase
    @Version
    private Long version;

    @Id
    @Column(length = 36) // For UUID from Keycloak's sub
    private String idUser; // Changed from Long to String, no @GeneratedValue

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String phoneNumber;

    @Size(max = 255)
    private String address;

    @NotNull(message = "Balance cannot be null")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // Password field removed

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private LocalDateTime updateDate;

    @Column(nullable = false)
    private String roles = "ROLE_USER"; // Populated from Keycloak token on creation

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }

    //Automatically updates the date of the last modification
    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}