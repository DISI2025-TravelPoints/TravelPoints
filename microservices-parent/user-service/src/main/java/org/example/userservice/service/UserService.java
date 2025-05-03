package org.example.userservice.service;

import org.example.userservice.builder.userbuilder.UserBuilder;
import org.example.userservice.dto.userdto.UserLoginDTO;
import org.example.userservice.dto.userdto.UserRegisterDTO;
import org.example.userservice.entity.Users;
import org.example.userservice.errorhandler.UserException;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.security.TokenBlacklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(ro|com|org)$");
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder encoder ;
    @Autowired
    private  TokenBlacklistService tokenBlacklistService;
    @Autowired
    private EmailService emailService;

    public UserService(UserRepository userRepository, JWTService jwtService, AuthenticationManager authenticationManager, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
    }

    public String registerUser(UserRegisterDTO dto) throws UserException {
        if (!emailPattern.matcher(dto.getEmail()).matches()) {
            throw new UserException("Emailul nu este valid: " + dto.getEmail());
        }

        Optional<Users> existingUser = userRepository.findUserByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new UserException("Emailul este deja înregistrat: " + dto.getEmail());
        }

        String hashPassword = encoder.encode(dto.getPassword());

        Users user = UserBuilder.generateEntityFromDTO(dto, hashPassword);

        userRepository.save(user);
        return "Utilizator înregistrat cu succes!";
    }

    public String login(UserLoginDTO dto) throws UserException {
        Users user = userRepository.findUserByEmail(dto.getEmail())
                .orElseThrow(() -> new UserException("Email inexistent."));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            return jwtService.generateToken(dto.getEmail());

        } catch (BadCredentialsException e) {
            throw new UserException("Parolă incorectă.");
        }
    }

    public String logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "Missing or invalid token.";
        }

        String token = authHeader.replace("Bearer ", "");
        tokenBlacklistService.blacklistToken(token);

        return "User successfully logged out.";
    }

    public String deleteUser(Long id) throws UserException {
        Optional<Users> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserException("Utilizatorul cu ID " + id + " nu a fost găsit.");
        }

        userRepository.deleteById(id);
        return "Utilizator șters cu succes.";
    }

    //pass-rest methods --------------------------------------------------------------

    public void initiatePasswordReset(String email) {
        System.out.println("Attempting to initiate password reset for email: " + email);

        Users user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with given email not found"));

        System.out.println("User found for email: " + email);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(10); // token valid 10 min
        user.setPasswordResetToken(token);
        user.setTokenExpiryDate(expiryDate);

        System.out.println("Generated token: " + token);

        try {
            userRepository.save(user);
            System.out.println("Saved user with reset token to the database.");

            emailService.sendPasswordResetEmail(user.getEmail(), token);
            System.out.println("Reset email sent to: " + email);

        } catch(Exception e) {
            System.err.println("Error during password reset initiation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void validatePasswordResetToken(String token) {
        Users user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token"));

        if (LocalDateTime.now().isAfter(user.getTokenExpiryDate())) {
            throw new IllegalArgumentException("Invalid or expired password reset token");
        }
    }

    public void resetPassword(String token, String newPassword) {
        validatePasswordResetToken(token);
        Users user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        user.setPassword(encoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setTokenExpiryDate(null);
        userRepository.save(user);
    }

}
