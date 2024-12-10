package com.team01.billage.chatting.dto.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class ChatroomWithRecentChatDTO {
    private Long chatroomId;
    private User buyer;
    private User seller;
    private Chat lastChat;
    private Product product;

    @Getter
    @AllArgsConstructor
    @ToString
    public static class User {
        private Long id;
        private String nickname;
        private String profileUrl;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class Chat {
        private String message;
        private LocalDateTime lastSentTime;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class Product {
        private Long id;
        private String name;
        private String imageUrl;
    }
}
