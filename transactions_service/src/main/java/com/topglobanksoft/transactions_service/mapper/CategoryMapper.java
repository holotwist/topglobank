package com.topglobanksoft.transactions_service.mapper;

import com.topglobanksoft.transactions_service.dto.category.CategoryCreateUpdateDTO;
import com.topglobanksoft.transactions_service.dto.category.CategoryDTO;
import com.topglobanksoft.transactions_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
/**
 * Maps between Category entities and DTOs
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDTO toDto(Category category);

    @Mapping(target = "categoryId", ignore = true)
        // @Mapping(target = "transactions", ignore = true) // If transactions field exists in Category entity
    Category toEntity(CategoryCreateUpdateDTO dto);

    @Mapping(target = "categoryId", ignore = true)
        // @Mapping(target = "transactions", ignore = true)
    void updateEntityFromDto(CategoryCreateUpdateDTO dto, @MappingTarget Category category);
}