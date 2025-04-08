package org.example.userservice.controller;

import org.example.userservice.dto.userdto.UserLoginDTO;
import org.example.userservice.dto.userdto.UserRegisterDTO;
import org.example.userservice.errorhandler.UserException;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userDTO) throws UserException {
        String message = userService.registerUser(userDTO);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO loginDTO) throws UserException {
        String jwt = userService.login(loginDTO);
        return ResponseEntity.ok(jwt);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            String response = userService.deleteUser(id);
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @RequestMapping("/")
    public String hello() {
        return "hello";
    }


}
