package com.team01.billage.chatting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckValidChatroomResponseDto {
    private Long chatroomId;
    private String productName;
    private String opponentName;

    public CheckValidChatroomResponseDto(Long chatroomId, String opponentName, String productName) {
        this.chatroomId = chatroomId;
        this.opponentName = opponentName;
        this.productName = productName;
    }
}
