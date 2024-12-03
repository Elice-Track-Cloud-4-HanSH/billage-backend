package com.team01.billage.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QFavoriteProduct;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.user.domain.QUsers;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OnSaleResponseDto> findAllOnSale(String email) {
        QProduct product = QProduct.product;
        QUsers seller = QUsers.users;
        QProductImage productImage = QProductImage.productImage;

        return queryFactory
            .select(Projections.constructor(
                OnSaleResponseDto.class,
                product.id,
                productImage.imageUrl,
                product.title
            ))
            .from(product)
            .join(product.seller, seller)
            .leftJoin(productImage)
            .on(productImage.product.eq(product)
                .and(productImage.thumbnail.eq("Y")))
            .where(seller.email.eq(email)
                .and(product.rentalStatus.eq(RentalStatus.AVAILABLE))
                .and(product.deletedAt.isNull()))
            .orderBy(product.updatedAt.desc())
            //.limit()
            .fetch();
    }

    @Override
    public List<ProductResponseDto> findAllProducts(Long userId, Long categoryId, String rentalStatus) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QFavoriteProduct favoriteProduct = QFavoriteProduct.favoriteProduct;

        // 동적 조건 처리
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.deletedAt.isNull());

        if(categoryId != 1L){
            builder.and(product.category.id.eq(categoryId));
        }

        if(rentalStatus.equals(RentalStatus.AVAILABLE.name())){
            builder.and(product.rentalStatus.eq(RentalStatus.AVAILABLE));
        }

        // 서브쿼리로 좋아요 개수 가져오기
        JPQLQuery<Long> favoriteCnt = JPAExpressions.select(favoriteProduct.count())
                .from(favoriteProduct)
                .where(favoriteProduct.product.id.eq(product.id));

        return queryFactory
                .select(Projections.fields(
                        ProductResponseDto.class,
                        product.id.as("productId"),
                        product.title,
                        product.updatedAt,
                        product.dayPrice,
                        product.weekPrice,
                        product.viewCount,
                        productImage.imageUrl.as("thumbnailUrl"),
                        favoriteProduct.id.isNotNull().as("favorite"),
                        ExpressionUtils.as(favoriteCnt, "favoriteCnt")
                ))
                .from(product)
                .leftJoin(productImage)
                .on(product.id.eq(productImage.product.id).
                        and(productImage.thumbnail.eq("Y")))
                .leftJoin(favoriteProduct)
                .on(product.id.eq(favoriteProduct.product.id).
                        and(favoriteProduct.user.id.eq(userId)))
                .where(builder)
                .orderBy(product.updatedAt.desc())
                .fetch();
    }
}
