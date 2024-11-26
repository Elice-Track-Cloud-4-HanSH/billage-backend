package com.team01.billage.chatting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team01.billage.chatting.domain.TestUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class ChatsResponseDto {
    private TestUser sender;
    private String message;
    private boolean isRead;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
