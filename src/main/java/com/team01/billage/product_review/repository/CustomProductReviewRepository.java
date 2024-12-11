package com.team01.billage.product_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface CustomProductReviewRepository {

    List<ShowReviewResponseDto> findByAuthor(long userId, Long lastStandard, Pageable pageable);

    List<ShowReviewResponseDto> findByProduct(Long productId, Long lastStandard, Pageable pageable);

    Optional<Double> scoreAverage(long productId);

    Optional<Integer> reviewCount(long productId);
}
