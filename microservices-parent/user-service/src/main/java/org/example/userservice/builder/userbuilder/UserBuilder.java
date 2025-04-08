package org.example.userservice.builder.userbuilder;

import org.example.userservice.dto.userdto.UserRegisterDTO;
import org.example.userservice.entity.Users;
import org.example.userservice.entity.UserRole;

import java.time.LocalDateTime;


public class UserBuilder {

    public static Users generateEntityFromDTO(UserRegisterDTO userDTO,String hashPassword){
        Users user = new Users();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(hashPassword);
        user.setRole(UserRole.Tourist);
        return user;
    }
}
