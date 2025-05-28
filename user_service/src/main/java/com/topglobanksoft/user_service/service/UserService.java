package com.topglobanksoft.user_service.service;

import com.topglobanksoft.user_service.dto.UserCreateDTO;
import com.topglobanksoft.user_service.dto.UserDTO;
import com.topglobanksoft.user_service.dto.UserUpdateDTO;
import org.springframework.security.oauth2.jwt.Jwt; // For passing JWT

import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    //Creates a new user profile from received data and JWT token information.
    UserDTO provisionUserProfile(UserCreateDTO userCreateDTO, Jwt jwt);
    UserDTO getUserById(String id); // Changed from Long
    UserDTO getUserByEmail(String email);
    //Returns the list of all existing users
    List<UserDTO> listAllUsers();
    //Updates non-sensitive user data using the user id
    UserDTO updateUser(String id, UserUpdateDTO userUpdateDTO);
    //Deletes a user from the database using the user id
    void deleteUser(String id); // Changed from Long
    //Updates the balance from a certain user
    void updateUserBalance(String userId, BigDecimal amount, boolean isCredit, String transactionTypeForLog);
}