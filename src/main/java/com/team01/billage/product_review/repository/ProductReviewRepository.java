package com.team01.billage.product_review.repository;

import com.team01.billage.product_review.domain.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long>,
    CustomProductReviewRepository {

}
