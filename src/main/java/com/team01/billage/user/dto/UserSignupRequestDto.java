package com.team01.billage.user.dto;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequestDto {
    private String nickname;
    private String email;
    private String password;
    private UserRole userRole;
    private Provider provider;
}