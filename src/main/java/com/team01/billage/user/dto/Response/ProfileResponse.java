package com.team01.billage.user.dto.Response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileResponse {
    private String nickname;
    private String description;
    private String imageUrl;

    @Builder
    public ProfileResponse(String nickname, String description, String imageUrl) {
        this.nickname = nickname;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
