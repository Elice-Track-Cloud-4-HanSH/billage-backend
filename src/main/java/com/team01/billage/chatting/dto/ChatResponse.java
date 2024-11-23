package com.team01.billage.chatting.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponse {
    private Long senderId;
    private String content;
    private LocalDateTime sendTime;
}
