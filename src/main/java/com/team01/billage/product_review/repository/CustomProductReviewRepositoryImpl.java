package com.team01.billage.product_review.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product_review.domain.QProductReview;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
import com.team01.billage.user.domain.QUsers;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductReviewRepositoryImpl implements CustomProductReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowReviewResponseDto> findByAuthor_email(String email) {
        QProductReview productReview = QProductReview.productReview;
        QUsers author = QUsers.users;
        QProduct product = QProduct.product;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    productReview.score,
                    productReview.content,
                    product.title
                    //, product.imageUrl
                )
            )
            .from(productReview)
            .join(productReview.product, product)
            .join(productReview.author, author)
            .where(author.email.eq(email))
            .fetch();
    }

    @Override
    public List<ShowReviewResponseDto> findByProduct_id(long id) {
        QProductReview productReview = QProductReview.productReview;
        QUsers author = QUsers.users;
        QProduct product = QProduct.product;

        return queryFactory.select(
                Projections.constructor(
                    ShowReviewResponseDto.class,
                    productReview.score,
                    productReview.content,
                    author.nickname,
                    author.imageUrl
                )
            )
            .from(productReview)
            .join(productReview.product, product)
            .join(productReview.author, author)
            .where(productReview.product.id.eq((int) id))
            .fetch();
    }
}

