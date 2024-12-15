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
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.rental_record.domain.QRentalRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CustomFavoriteRepositoryImpl implements CustomFavoriteRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        QFavoriteProduct favoriteProduct = QFavoriteProduct.favoriteProduct;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QRentalRecord rentalRecord = QRentalRecord.rentalRecord;

        // 서브쿼리로 좋아요 개수 가져오기
        JPQLQuery<Long> favoriteCnt = JPAExpressions.select(favoriteProduct.count())
                .from(favoriteProduct)
                .where(favoriteProduct.product.id.eq(product.id));

        // 상품 상태가 RENTED이면 expectedReturnDate 표시
        JPQLQuery<LocalDate> expectedReturnDate = JPAExpressions.select(rentalRecord.expectedReturnDate)
                .from(rentalRecord)
                .where(product.rentalStatus.eq(RentalStatus.RENTED)
                        .and(rentalRecord.returnDate.isNull())
                        .and(rentalRecord.product.id.eq(product.id)));

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
                        Expressions.asBoolean(true).as("favorite"),
                        ExpressionUtils.as(favoriteCnt, "favoriteCnt"),
                        ExpressionUtils.as(expectedReturnDate, "expectedReturnDate")
                ))
                .from(favoriteProduct)
                .leftJoin(product)
                .on(favoriteProduct.product.id.eq(product.id))
                .leftJoin(productImage)
                .on(product.id.eq(productImage.product.id).
                        and(productImage.thumbnail.eq("Y")))
                .where(favoriteProduct.user.id.eq(userId).and(product.deletedAt.isNull()))
                .orderBy(favoriteProduct.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

}
