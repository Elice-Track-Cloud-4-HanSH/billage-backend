package com.team01.billage.user_review.repository;

import com.team01.billage.user_review.domain.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {

}
