package com.team01.billage.product_review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WriteReviewRequestDto {

    @Schema(description = "후기 별점", example = "4", minimum = "0", maximum = "5")
    @NotNull(message = "별점을 입력해주세요.")
    @Min(0)
    @Max(5)
    private int score;

    @Schema(description = "후기 내용", example = "정말 좋았습니다.")
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
