package com.topglobanksoft.transactions_service.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateUpdateDTO {
    @NotBlank(message = "Category name cannot be empty")
    @Size(max = 50, message = "Category name must be less than 50 characters")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}