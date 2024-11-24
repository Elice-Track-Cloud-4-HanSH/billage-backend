package com.team01.billage.product_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;

public interface CustomProductReviewRepository {

    List<ShowReviewResponseDto> findByAuthor_email(String email);

    List<ShowReviewResponseDto> findByProduct_id(long id);
}
