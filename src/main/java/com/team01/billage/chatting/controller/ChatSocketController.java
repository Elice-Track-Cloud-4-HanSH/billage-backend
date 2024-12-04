package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.dto.ChatMessage;
import com.team01.billage.chatting.dto.ChatResponseDto;
import com.team01.billage.chatting.service.ChatSocketService;
import com.team01.billage.user.domain.Users;
import com.team01.billage.utils.DetermineUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

private final ChatSocketService chatSocketService;
    private final DetermineUser determineUser;

    @MessageMapping("/chat/{chatroomId}")
    @SendTo("/sub/chat/{chatroomId}")
    public ChatResponseDto chat(
            @DestinationVariable Long chatroomId,
            ChatMessage message,
            java.security.Principal principal
    ) {
        Users sender = determineUser.determineUser(principal.getName());

        ChatResponseDto responseDto = chatSocketService.insertChat(chatroomId, sender, message);
        return responseDto;
    }

    @MessageMapping("/chat/chatting/{chatId}")
    public void ack(@DestinationVariable Long chatId, java.security.Principal principal) {
        chatSocketService.markAsRead(chatId);
    }
}