package com.team01.billage.user.dto.Request;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import io.swagger.v3.oas.annotations.info.Contact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequestDto {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(max = 15, message = "닉네임은 15자 이하여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;
    private UserRole userRole;
    private Provider provider;


}