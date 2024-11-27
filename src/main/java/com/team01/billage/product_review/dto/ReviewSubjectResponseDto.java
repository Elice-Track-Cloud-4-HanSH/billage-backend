package com.team01.billage.product_review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSubjectResponseDto {

    private String imageUrl;
    private String subject;
}
