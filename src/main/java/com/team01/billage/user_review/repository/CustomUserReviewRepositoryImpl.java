package com.team01.billage.user_review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.user.domain.QUsers;
import com.team01.billage.user_review.domain.QUserReview;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomUserReviewRepositoryImpl implements CustomUserReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowReviewResponseDto> findByAuthor(long userId, Long lastStandard,
        Pageable pageable) {
        QUserReview userReview = QUserReview.userReview;
        QUsers target = QUsers.users;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(userReview.author.id.eq(userId));

        if (lastStandard != null) {
            condition.and(userReview.id.lt(lastStandard));
        }

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
            .where(condition)
            .orderBy(userReview.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public List<ShowReviewResponseDto> findByTarget(long userId, Long lastStandard,
        Pageable pageable) {
        QUserReview userReview = QUserReview.userReview;
        QUsers author = QUsers.users;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(userReview.target.id.eq(userId));

        if (lastStandard != null) {
            condition.and(userReview.id.lt(lastStandard));
        }

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
            .where(condition)
            .orderBy(userReview.id.desc())
            .limit(pageable.getPageSize() + 1)
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
