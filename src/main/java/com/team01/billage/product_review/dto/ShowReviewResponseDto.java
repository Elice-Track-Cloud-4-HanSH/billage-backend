package com.team01.billage.product_review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowReviewResponseDto {

    private long reviewId;
    private int score;
    private String content;
    private long id;
    private String imageUrl;
    private String subject;
}
