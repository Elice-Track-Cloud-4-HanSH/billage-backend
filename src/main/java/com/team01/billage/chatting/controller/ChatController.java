package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestUser;
import com.team01.billage.chatting.dto.ChatMessage;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.chatting.repository.TestUserRepository;
import com.team01.billage.chatting.store.WebSocketSessionStore;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final TestUserRepository testUserRepository;

    @MessageMapping("/chat/{chatroomId}")
    @SendTo("/sub/chat/{chatroomId}")
    public ChatMessage chat(
            @DestinationVariable Long chatroomId,
            SimpMessageHeaderAccessor headerAccessor,
            ChatMessage message
    ) {
        System.out.println(message);

        String sessionId = headerAccessor.getSessionId();
        Long senderId = WebSocketSessionStore.get(sessionId);

        TestUser sender = testUserRepository.findById(senderId).orElse(null);
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId).orElse(null);
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(message.getMessage())
                .build();

        chatRepository.save(chat);

        return message;
    }
}