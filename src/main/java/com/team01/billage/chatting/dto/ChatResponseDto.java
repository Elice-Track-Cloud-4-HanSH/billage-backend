package com.team01.billage.chatting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team01.billage.chatting.domain.TestUser;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ChatResponseDto {
    private Long chatId;
    private TestUser sender;
    private String message;
    private boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
