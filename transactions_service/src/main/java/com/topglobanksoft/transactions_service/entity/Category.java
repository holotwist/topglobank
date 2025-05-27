package com.topglobanksoft.transactions_service.entity;

import jakarta.persistence.*; // Ensure this is the JPA Id
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// import org.springframework.data.annotation.Id; // Remove this if using JPA @Id

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @jakarta.persistence.Id // JPA standard ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId; // Renamed from idCategoria for consistency with id

    @NotBlank(message = "Category name cannot be empty")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name; // Renamed from nombre

    @Size(max = 255)
    private String description; // Renamed from descripcion

    // Example of bi-directional mapping (optional, can lead to issues if not handled carefully)
    // @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    // private List<Transaction> transactions;

    // The 'private Long id;' field was redundant and conflicting. Removed. it breaks other microservices
}