package org.example.userservice.builder.userbuilder;

import lombok.Builder;
import org.example.userservice.mapper.dto.userdto.UserDTO;
import org.example.userservice.mapper.dto.userdto.UserRegisterDTO;
import org.example.userservice.mapper.entity.Users;
import org.example.userservice.mapper.entity.UserRole;

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
