package com.topglobanksoft.bank_accounts_service.mapper;

import com.topglobanksoft.bank_accounts_service.dto.AccountCreateDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountDTO;
import com.topglobanksoft.bank_accounts_service.dto.AccountUpdateDTO;
import com.topglobanksoft.bank_accounts_service.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDTO toDto(Account account);

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "userId", ignore = true) // userId will be set separately in service
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Account toEntity(AccountCreateDTO accountCreateDTO);

    @Mapping(target = "accountId", ignore = true)
    @Mapping(target = "userId", ignore = true) // Should not be updatable
    @Mapping(target = "creationDate", ignore = true) // Should not be updatable
    @Mapping(target = "updateDate", ignore = true) // Handled by @PreUpdate
    void updateEntityFromDto(AccountUpdateDTO accountUpdateDTO, @MappingTarget Account account);
}