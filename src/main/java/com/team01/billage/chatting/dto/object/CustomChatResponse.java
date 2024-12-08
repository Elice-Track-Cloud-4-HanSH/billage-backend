package com.team01.billage.chatting.dto.object;

import com.team01.billage.chatting.domain.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CustomChatResponse {
    private final String message;
    private final LocalDateTime lastSentTime;

    public CustomChatResponse(Chat lastChat) {
        this.message = lastChat.getMessage();
        this.lastSentTime = lastChat.getCreatedAt();
    }
}
