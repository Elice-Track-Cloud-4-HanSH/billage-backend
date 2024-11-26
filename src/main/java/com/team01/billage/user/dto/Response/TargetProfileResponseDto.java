package com.team01.billage.user.dto.Response;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TargetProfileResponseDto {

    private String imageUrl;
    private String nickname;
    private String description;
    private Double avgScore;
    // user review count??
    List<ShowReviewResponseDto> reviews;
}
