package com.team01.billage.chatting.dto.object;

import com.team01.billage.user.domain.Users;
import lombok.Getter;

@Getter
public class CustomChatResponseUser {
    private final Long id;
    private final String nickname;

    public CustomChatResponseUser(Users user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}