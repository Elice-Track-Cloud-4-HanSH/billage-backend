package com.team01.billage.product_review.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class WriteReviewRequestDto {

    @NotNull
    @Min(0)
    private int score;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
