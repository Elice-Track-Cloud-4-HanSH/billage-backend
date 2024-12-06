package com.team01.billage.user_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;
import java.util.Optional;

public interface CustomUserReviewRepository {

    List<ShowReviewResponseDto> findByAuthor_email(String email);

    List<ShowReviewResponseDto> findByTarget_nickname(long userId);

    Optional<Double> scoreAverage(long userId);

    Optional<Integer> reviewCount(long userId);
}
