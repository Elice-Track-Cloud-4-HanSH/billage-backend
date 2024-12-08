package com.team01.billage.chatting.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ChatMessage {
    @Getter
    public static class Chatting {
        private String message; // 메시지
    }

    @Getter
    @AllArgsConstructor
    public static class UnreadCount {
        private Long unreadCount;
    }

    @Getter
    @AllArgsConstructor
    public static class UnreadCountDelta {
        private String operation;
        private int value;
    }

}
