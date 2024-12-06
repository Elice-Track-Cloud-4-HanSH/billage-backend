package com.team01.billage.product_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;
import java.util.Optional;

public interface CustomProductReviewRepository {

    List<ShowReviewResponseDto> findByAuthor_email(String email);

    List<ShowReviewResponseDto> findByProduct_id(Long id);

    Optional<Double> scoreAverage(long productId);

    Optional<Integer> reviewCount(long productId);
}
