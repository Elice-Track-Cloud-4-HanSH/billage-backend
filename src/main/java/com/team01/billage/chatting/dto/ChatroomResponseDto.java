package com.team01.billage.chatting.dto;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.TestUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatroomResponseDto {
    private Long chatroomId;
    private TestUser buyer;
    private TestUser seller;
    private CustomChatResponse lastChat;

    public ChatroomResponseDto(Long chatroomId, TestUser buyer, TestUser seller, Chat lastChat) {
        this.chatroomId = chatroomId;
        this.buyer = buyer;
        this.seller = seller;
        this.lastChat = new CustomChatResponse(lastChat);
    }
}

@Getter
class CustomChatResponse {
    private final String message;
    private final LocalDateTime lastSentTime;
    private final TestUser sender;

    public CustomChatResponse(Chat lastChat) {
        this.message = lastChat.getMessage();
        this.lastSentTime = lastChat.getCreatedAt();
        this.sender = lastChat.getSender();
    }
}
