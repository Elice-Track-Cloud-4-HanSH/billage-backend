package com.team01.billage.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckValidChatroomRequestDto {
    private Long buyerId;
    private Long sellerId;
    private Long productId;
}
