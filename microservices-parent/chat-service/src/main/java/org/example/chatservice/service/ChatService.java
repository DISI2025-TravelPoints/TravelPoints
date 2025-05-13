package org.example.chatservice.service;

import lombok.RequiredArgsConstructor;
import org.example.chatservice.repository.ChatRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
}
