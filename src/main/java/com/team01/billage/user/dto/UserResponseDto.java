package com.team01.billage.user.dto;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private  long id;
    private String nickname;
    private String email;
    private String imageUrl;
    private String description;
    private UserRole role;
    private Provider provider;

}
