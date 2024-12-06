package com.team01.billage.user.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetProfileResponseDto {

    private String imageUrl;
    private String nickname;
    private String description;
    private Double avgScore;
    private int reviewCount;
}
