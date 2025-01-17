package com.team01.billage.product_review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product_review.domain.QProductReview;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.user.domain.QUsers;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomProductReviewRepositoryImpl implements CustomProductReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowReviewResponseDto> findByAuthor(long userId, Long lastStandard,
        Pageable pageable) {
        QProductReview productReview = QProductReview.productReview;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(productReview.author.id.eq(userId));

        if (lastStandard != null) {
            condition.and(productReview.id.lt(lastStandard));
        }

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    productReview.id,
                    productReview.score,
                    productReview.content,
                    product.id,
                    productImage.imageUrl,
                    product.title
                )
            )
            .from(productReview)
            .join(productReview.rentalRecord.product, product)
            .leftJoin(productImage)
            .on(productImage.product.eq(product)
                .and(productImage.thumbnail.eq("Y")))
            .where(condition)
            .orderBy(productReview.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public List<ShowReviewResponseDto> findByProduct(Long productId, Long lastStandard,
        Pageable pageable) {
        QProductReview productReview = QProductReview.productReview;
        QUsers author = QUsers.users;

        BooleanBuilder condition = new BooleanBuilder();
        condition.and(productReview.rentalRecord.product.id.eq(productId));

        if (lastStandard != null) {
            condition.and(productReview.id.lt(lastStandard));
        }

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    productReview.id,
                    productReview.score,
                    productReview.content,
                    author.id,
                    author.imageUrl,
                    author.nickname
                )
            )
            .from(productReview)
            .join(productReview.author, author)
            .where(condition)
            .orderBy(productReview.id.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public Optional<Double> scoreAverage(long productId) {
        QProductReview productReview = QProductReview.productReview;

        Double averageScore = queryFactory
            .select(productReview.score.avg())
            .from(productReview)
            .where(productReview.rentalRecord.product.id.eq(productId))
            .fetchOne();

        return Optional.ofNullable(averageScore);
    }

    @Override
    public Optional<Integer> reviewCount(long productId) {
        QProductReview productReview = QProductReview.productReview;

        Integer count = queryFactory
            .select(productReview.count().intValue())
            .from(productReview)
            .where(productReview.rentalRecord.product.id.eq(productId))
            .fetchOne();

        return Optional.ofNullable(count);
    }
}

