package org.example.chatservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.chatservice.mapper.dto.UserSyncDTO;
import org.example.chatservice.mapper.entity.User;
import org.example.chatservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void syncUsers(UserSyncDTO request){
        Set<User> incomingUsers = request.getUsers();
        Set<User> existingUsers = new HashSet<>(userRepository.findAll());

        Set<User> usersToAdd = new HashSet<>(incomingUsers);
        usersToAdd.removeAll(existingUsers);

        Set<User> usersToDelete = new HashSet<>(existingUsers);
        usersToDelete.removeAll(incomingUsers);


        if (!usersToAdd.isEmpty()) {
            userRepository.saveAll(usersToAdd);
        }

        if (!usersToDelete.isEmpty()) {
            userRepository.deleteAll(usersToDelete);
        }
    }
}
