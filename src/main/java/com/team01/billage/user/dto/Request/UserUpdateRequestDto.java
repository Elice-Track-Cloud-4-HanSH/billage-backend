package com.team01.billage.user.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequestDto {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 15, message = "닉네임은 15자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;

    private String imageUrl;

    @NotBlank(message = "자기소개는 필수 입력값입니다.")
    @Size(min = 5, max = 50, message = "자기소개는 5자 이상 50자 이하여야 합니다.")
    private String description;

    @Builder
    public UserUpdateRequestDto(String nickname, String password, String imageUrl, String description) {
        this.nickname = nickname;
        this.password = password;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
