package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.dto.ChatMessage;
import com.team01.billage.chatting.dto.ChatResponseDto;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.chatting.store.WebSocketSessionStore;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
//    private final TestUserRepository testUserRepository;

    private final UserRepository userRepository;

    @MessageMapping("/chat/{chatroomId}")
    @SendTo("/sub/chat/{chatroomId}")
    public ChatResponseDto chat(
            @DestinationVariable Long chatroomId,
            SimpMessageHeaderAccessor headerAccessor,
            ChatMessage message
    ) {
        String sessionId = headerAccessor.getSessionId();
        Long senderId = WebSocketSessionStore.get(sessionId);

        Users sender =  userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Invalid sender: User not found."));
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow(() -> new IllegalArgumentException("Invalid chatroom: Chatroom not found."));
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .message(message.getMessage())
                .build();

        chatRepository.save(chat);

        return chat.toChatResponse();
    }

    @MessageMapping("/chat/chatting/{chatId}")
    public void ack(@DestinationVariable Long chatId) {
        chatRepository.markAsRead(chatId);
    }
}