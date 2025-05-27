package com.topglobanksoft.budget_service.mapper;

import com.topglobanksoft.budget_service.dto.BudgetCreateDTO;
import com.topglobanksoft.budget_service.dto.BudgetDTO;
import com.topglobanksoft.budget_service.dto.BudgetUpdateDTO;
import com.topglobanksoft.budget_service.entity.Budget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BudgetMapper {
    BudgetMapper INSTANCE = Mappers.getMapper(BudgetMapper.class);

    @Mapping(target = "amountRemaining", ignore = true) // Calculated in DTO getter
    BudgetDTO toDto(Budget budget);

    @Mapping(target = "budgetId", ignore = true)
    @Mapping(target = "userId", ignore = true) // Set in service
    @Mapping(target = "amountSpent", ignore = true) // Defaulted in entity or service
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "version", ignore = true)
    Budget toEntity(BudgetCreateDTO budgetCreateDTO);

    @Mapping(target = "budgetId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "categoryId", ignore = true) // Usually not updatable
    @Mapping(target = "year", ignore = true)       // Usually not updatable
    @Mapping(target = "month", ignore = true)      // Usually not updatable
    @Mapping(target = "amountSpent", ignore = true) // Updated by Kafka listener
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    // Version is handled by JPA, not directly mapped from DTO during update
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(BudgetUpdateDTO budgetUpdateDTO, @MappingTarget Budget budget);
}