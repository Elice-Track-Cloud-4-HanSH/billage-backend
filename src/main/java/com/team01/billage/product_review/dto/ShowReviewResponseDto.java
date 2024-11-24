package com.team01.billage.product_review.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowReviewResponseDto {

    private int score;
    private String content;
    private String imageUrl;
    private String subject;
}
