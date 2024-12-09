package com.team01.billage.chatting.dto.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatWithSenderDTO {
    private Long chatId;
    private User sender;
    private Message message;
    private boolean read;

    @Getter
    @AllArgsConstructor
    public static class User {
        private Long id;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String message;
        private LocalDateTime createdAt;
    }
}
