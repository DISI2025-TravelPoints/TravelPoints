package org.example.userservice.controller;

import org.example.userservice.dto.userdto.*;
import java.util.List;
import org.example.userservice.errorhandler.UserException;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final UserService userService;

    public UserController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }


    @GetMapping("/id-by-email")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        Long userId = userService.getUserIdByEmail(email);
        return ResponseEntity.ok(userId);
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userDTO) throws UserException {
        String message = userService.registerUser(userDTO);
        userService.syncUsers(restTemplate);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginDTO loginDTO) throws UserException {
        String jwt = userService.login(loginDTO);
        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        String message = userService.logout(authHeader);

        if (message.equals("Missing or invalid token.")) {
            return ResponseEntity.badRequest().body(message);
        }

        return ResponseEntity.ok(message);
    }

    @GetMapping("/protected-test")
    public ResponseEntity<String> protectedTest() {
        return ResponseEntity.ok("Access granted: your token is valid and not blacklisted.");
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

    @PostMapping("/initiate-password-reset")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody EmailDTO emailDto) {
        try {
            userService.initiatePasswordReset(emailDto.getEmail());
            return ResponseEntity.ok("Password reset link sent to your email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/validate-password-reset-token")
    public ResponseEntity<String> validatePasswordResetToken(@RequestParam String token) {
        try {
            userService.validatePasswordResetToken(token);
            return ResponseEntity.ok("Token is valid.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO dto) {
        try {
            userService.resetPassword(dto.getToken(), dto.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/me")
    public ResponseEntity<LoggedInUserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String token = authHeader.replace("Bearer ", "");
        LoggedInUserDTO dto = userService.getLoggedInUser(token);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/names-by-ids")
    public ResponseEntity<List<UserDTO>> getUserNamesByIds(@RequestBody List<Long> userIds) {
        List<UserDTO> users = userService.getUsersByIds(userIds);
        return ResponseEntity.ok(users);
    }

}
