package com.team01.billage.user.dto.Response;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetProfileResponseDto {

    @Parameter(description = "유저 프로필 이미지", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Parameter(description = "유저 닉네임", example = "김땡땡")
    private String nickname;

    @Parameter(description = "유저의 소개글", example = "안녕하세요. 좋은 거래해요.")
    private String description;

    @Parameter(description = "유저에 대한 사용자 후기의 별점 평균", example = "3.5")
    private Double avgScore;

    @Parameter(description = "유저에 대한 사용자 후기 총 개수", example = "10")
    private Integer reviewCount;
}
