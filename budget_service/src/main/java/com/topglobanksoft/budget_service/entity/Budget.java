package com.topglobanksoft.budget_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets", uniqueConstraints = {
        // Quote column names in unique constraint definition as well
        @UniqueConstraint(columnNames = {"user_id", "category_id", "\"year\"", "\"month\""})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long budgetId;

    @NotBlank(message = "Budget name cannot be empty")
    @Size(max = 100)
    @Column(name = "\"name\"", nullable = false, length = 100) // Quoted "name"
    private String name;

    @NotNull(message = "El monto total es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto total debe ser positivo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal initialAmount;

    @NotNull(message = "El monto gastado no puede ser nulo")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amountSpent = BigDecimal.ZERO;

    @NotNull(message = "El ID del usuario es obligatorio")
    @Column(nullable = false, name = "user_id", updatable = false, length = 36)
    private String userId;

    @NotNull(message = "El ID de la categoría es obligatorio")
    @Column(nullable = false, name = "category_id")
    private Long categoryId;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "Invalid year")
    @Column(name = "\"year\"", nullable = false) // Quoted "year"
    private Integer year;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "Mes inválido")
    @Max(value = 12, message = "Mes inválido")
    @Column(name = "\"month\"", nullable = false) // Quoted "month"
    private Integer month;

    @Column(updatable = false)
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
        updateDate = LocalDateTime.now();
        if (amountSpent == null) {
            amountSpent = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}