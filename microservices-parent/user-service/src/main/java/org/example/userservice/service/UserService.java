package org.example.userservice.service;

import lombok.RequiredArgsConstructor;
import org.example.userservice.builder.userbuilder.UserBuilder;
import org.example.userservice.mapper.dto.userdto.UserDTO;
import org.example.userservice.mapper.dto.userdto.UserLoginDTO;
import org.example.userservice.mapper.dto.userdto.UserRegisterDTO;
import org.example.userservice.mapper.entity.Users;
import org.example.userservice.errorhandler.UserException;
import org.example.userservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(ro|com|org)$");
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder encoder ;

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(dto.getEmail());
        } else {
            throw new UserException("Email sau parolă invalidă");
        }
    }
    public String deleteUser(Long id) throws UserException {
        Optional<Users> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserException("Utilizatorul cu ID " + id + " nu a fost găsit.");
        }

        userRepository.deleteById(id);
        return "Utilizator șters cu succes.";
    }

}
