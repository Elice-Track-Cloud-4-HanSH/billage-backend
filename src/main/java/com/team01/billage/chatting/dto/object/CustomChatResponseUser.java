package com.team01.billage.chatting.dto.object;

import com.team01.billage.user.domain.Users;
import lombok.Getter;

@Getter
public class CustomChatResponseUser {
    private final Long id;
    private final String nickname;
    private final String profileImage;

    public CustomChatResponseUser(Long id, String nickname) {
        this(id, nickname, null);
    }

    public CustomChatResponseUser(Long id, String nickname, String profileImage) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = "/images/default_profile.png".equals(profileImage) ? null : profileImage;
    }

    public CustomChatResponseUser(Users user) {
        this(user.getId(), user.getNickname(), user.getImageUrl());
    }
}