package com.team01.billage.product.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QFavoriteProduct;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomFavoriteRepositoryImpl implements CustomFavoriteRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductResponseDto> findAllByUserId(Long userId) {
        QFavoriteProduct favoriteProduct = QFavoriteProduct.favoriteProduct;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        // 서브쿼리로 좋아요 개수 가져오기
        JPQLQuery<Long> favoriteCnt = JPAExpressions.select(favoriteProduct.count())
                .from(favoriteProduct)
                .where(favoriteProduct.product.id.eq(product.id));

        return queryFactory
                .select(Projections.constructor(
                        ProductResponseDto.class,
                        product.id.as("productId"),
                        product.title,
                        product.updatedAt,
                        product.dayPrice,
                        product.weekPrice,
                        product.viewCount,
                        productImage.imageUrl.as("thumbnailUrl"),
                        Expressions.asBoolean(true).as("favorite"),
                        ExpressionUtils.as(favoriteCnt, "favoriteCnt")
                ))
                .from(favoriteProduct)
                .leftJoin(product)
                .on(favoriteProduct.product.id.eq(product.id))
                .leftJoin(productImage)
                .on(product.id.eq(productImage.product.id).
                        and(productImage.thumbnail.eq("Y")))
                .where(favoriteProduct.user.id.eq(userId).and(product.deletedAt.isNull()))
                .orderBy(favoriteProduct.createdAt.desc())
                .fetch();
    }

}
