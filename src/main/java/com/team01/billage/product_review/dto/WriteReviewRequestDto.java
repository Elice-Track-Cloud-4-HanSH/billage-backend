package com.team01.billage.product_review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WriteReviewRequestDto {

    @NotBlank(message = "별점을 설정해주세요.")
    private int score;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
