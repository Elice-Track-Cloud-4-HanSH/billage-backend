package com.team01.billage.product_review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowReviewResponseDto {

    @Schema(description = "후기 ID", example = "1")
    private long reviewId;

    @Schema(description = "후기 별점", example = "5")
    private int score;

    @Schema(description = "후기 내용", example = "상품이 훌륭합니다.")
    private String content;

    @Schema(description = "후기 대상 ID", example = "100")
    private long id;

    @Schema(description = "후기 대상 이미지 URL", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "후기 대상 닉네임 또는 제목", example = "디지털 카메라, 김땡땡")
    private String subject;
}
