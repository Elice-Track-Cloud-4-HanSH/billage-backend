package com.team01.billage.user.dto.Request;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import io.swagger.v3.oas.annotations.info.Contact;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequestDto {
    private String nickname;
    private String email;
    private String password;
    private UserRole userRole;
    private Provider provider;


}