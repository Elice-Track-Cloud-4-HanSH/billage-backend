package com.team01.billage.user_review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.user.domain.QUsers;
import com.team01.billage.user_review.domain.QUserReview;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserReviewRepositoryImpl implements CustomUserReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowReviewResponseDto> findByAuthor_email(String email) {
        QUserReview userReview = QUserReview.userReview;
        QUsers target = QUsers.users;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    userReview.id,
                    userReview.score,
                    userReview.content,
                    target.id,
                    target.imageUrl,
                    target.nickname
                )
            )
            .from(userReview)
            .join(userReview.target, target)
            .where(userReview.author.email.eq(email))
            .orderBy(userReview.createdAt.desc())
            //.limit()
            .fetch();
    }

    @Override
    public List<ShowReviewResponseDto> findByTarget_nickname(long userId) {
        QUserReview userReview = QUserReview.userReview;
        QUsers author = QUsers.users;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    userReview.id,
                    userReview.score,
                    userReview.content,
                    author.id,
                    author.imageUrl,
                    author.nickname
                )
            )
            .from(userReview)
            .join(userReview.author, author)
            .where(userReview.target.id.eq(userId))
            .orderBy(userReview.createdAt.desc())
            //.limit()
            .fetch();
    }

    @Override
    public Optional<Double> scoreAverage(long userId) {
        QUserReview userReview = QUserReview.userReview;

        Double averageScore = queryFactory
            .select(userReview.score.avg())
            .from(userReview)
            .where(userReview.target.id.eq(userId))
            .fetchOne();

        return Optional.ofNullable(averageScore);
    }

    @Override
    public Optional<Integer> reviewCount(long userId) {
        QUserReview userReview = QUserReview.userReview;

        Integer count = queryFactory
            .select(userReview.count().intValue())
            .from(userReview)
            .where(userReview.target.id.eq(userId))
            .fetchOne();

        return Optional.ofNullable(count);
    }
}
