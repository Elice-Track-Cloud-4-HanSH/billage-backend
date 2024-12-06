package com.team01.billage.user.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class ProfileUpdateRequest {
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotBlank(message = "자기소개는 필수입니다.")
    private String description;

    private String imageUrl;

    public ProfileUpdateRequest(String nickname, String description, String imageUrl) {
        this.nickname = nickname;
        this.description = description;
        this.imageUrl = imageUrl;
    }

}