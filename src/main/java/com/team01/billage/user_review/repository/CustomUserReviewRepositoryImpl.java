package com.team01.billage.user_review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.user.domain.QUsers;
import com.team01.billage.user_review.domain.QUserReview;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserReviewRepositoryImpl implements CustomUserReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowReviewResponseDto> findByAuthor_email(String email) {
        QUserReview userReview = QUserReview.userReview;
        QUsers author = QUsers.users;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    userReview.id,
                    userReview.score,
                    userReview.content,
                    author.id,
                    author.nickname,
                    author.imageUrl
                )
            )
            .from(userReview)
            .join(userReview.author, author)
            .where(author.email.eq(email))
            .fetch();
    }

    @Override
    public List<ShowReviewResponseDto> findByTarget_id(long id) {
        QUserReview userReview = QUserReview.userReview;
        QUsers target = QUsers.users;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    userReview.id,
                    userReview.score,
                    userReview.content,
                    target.id,
                    target.nickname,
                    target.imageUrl
                )
            )
            .from(userReview)
            .join(userReview.target, target)
            .where(target.id.eq(id))
            .fetch();
    }
}
