package com.team01.billage.product_review.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShowReviewResponseDto {

    private long reviewId;
    private int score;
    private String content;
    private long id;
    private String imageUrl;
    private String subject;
}
