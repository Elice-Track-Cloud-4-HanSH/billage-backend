package com.team01.billage.product_review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubjectResponseDto {

    @Schema(description = "후기 대상 이미지", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "후기 대상 닉네임 또는 제목", example = "김땡땡, 디지털 카메라")
    private String subject;
}
