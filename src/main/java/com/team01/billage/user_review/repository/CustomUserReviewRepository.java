package com.team01.billage.user_review.repository;

import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import java.util.List;

public interface CustomUserReviewRepository {

    List<ShowReviewResponseDto> findByAuthor_email(String email);

    List<ShowReviewResponseDto> findByTarget_id(long id);
}
