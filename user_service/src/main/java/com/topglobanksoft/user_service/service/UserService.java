package com.topglobanksoft.user_service.service;

import com.topglobanksoft.user_service.dto.UserCreateDTO;
import com.topglobanksoft.user_service.dto.UserDTO;
import com.topglobanksoft.user_service.dto.UserUpdateDTO;
import org.springframework.security.oauth2.jwt.Jwt; // For passing JWT

import java.math.BigDecimal;
import java.util.List;

public interface UserService {
    UserDTO provisionUserProfile(UserCreateDTO userCreateDTO, Jwt jwt);
    UserDTO getUserById(String id); // Changed from Long
    UserDTO getUserByEmail(String email);
    List<UserDTO> listAllUsers();
    UserDTO updateUser(String id, UserUpdateDTO userUpdateDTO);
    void deleteUser(String id); // Changed from Long
    void updateUserBalance(String userId, BigDecimal amount, boolean isCredit, String transactionTypeForLog);
}