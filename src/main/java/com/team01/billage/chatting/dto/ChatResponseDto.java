package com.team01.billage.chatting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team01.billage.user.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class ChatResponseDto {
    private Long chatId;
    private Users sender;
    private String message;
    private boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
