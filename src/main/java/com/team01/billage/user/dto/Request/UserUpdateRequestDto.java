package com.team01.billage.user.dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {

    private String nickname;
    private String password;
    private String imageUrl;
    private String description;

    @Builder
    public UserUpdateRequestDto(String nickname, String password, String imageUrl, String description) {
        this.nickname = nickname;
        this.password = password;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
