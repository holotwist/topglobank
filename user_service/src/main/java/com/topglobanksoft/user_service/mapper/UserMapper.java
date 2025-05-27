package com.topglobanksoft.user_service.mapper;

import com.topglobanksoft.user_service.dto.UserCreateDTO;
import com.topglobanksoft.user_service.dto.UserDTO;
import com.topglobanksoft.user_service.dto.UserUpdateDTO;
import com.topglobanksoft.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);

    @Mapping(target = "idUser", ignore = true) // Set from Keycloak 'sub' in service
    @Mapping(target = "email", ignore = true) // Set from Keycloak 'email' in service
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "roles", ignore = true) // Set from Keycloak roles in service
    @Mapping(target = "version", ignore = true)
    User toEntity(UserCreateDTO userCreateDTO);

    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "email", ignore = true) // Email changes should be handled via Keycloak
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}