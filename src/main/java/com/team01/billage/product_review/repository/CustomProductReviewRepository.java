package com.team01.billage.product_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;
import java.util.Optional;

public interface CustomProductReviewRepository {

    List<ShowReviewResponseDto> findByAuthor(long userId);

    List<ShowReviewResponseDto> findByProduct(Long productId);

    Optional<Double> scoreAverage(long productId);

    Optional<Integer> reviewCount(long productId);
}
