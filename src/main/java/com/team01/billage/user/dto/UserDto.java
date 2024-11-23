package com.team01.billage.user.dto;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private String imageUrl;
    private String description;
    private UserRole role;
    private Provider provider;
}

