package com.team01.billage.chatting.dto.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatWithChatroomAndBSDTO {
    private Long chatroomId;
    private User buyer;
    private User seller;
    private Long senderId;

    @Getter
    @AllArgsConstructor
    public static class User {
        Long id;
    }
}
