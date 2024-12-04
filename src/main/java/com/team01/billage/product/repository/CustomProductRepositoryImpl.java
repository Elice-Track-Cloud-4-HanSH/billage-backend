package com.team01.billage.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.category.dto.CategoryProductResponseDto;
import com.team01.billage.product.domain.QFavoriteProduct;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product.dto.*;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.rental_record.domain.QRentalRecord;
import com.team01.billage.user.domain.QUsers;

import java.time.LocalDate;
import java.util.ArrayList;
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
        QRentalRecord rentalRecord = QRentalRecord.rentalRecord;

        // 동적 조건 처리
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.deletedAt.isNull());

        if (categoryId != 1L) {
            builder.and(product.category.id.eq(categoryId));
        }

        if (rentalStatus.equals(RentalStatus.AVAILABLE.name())) {
            builder.and(product.rentalStatus.eq(RentalStatus.AVAILABLE));
        }

        // 서브쿼리로 좋아요 개수 가져오기
        JPQLQuery<Long> favoriteCnt = JPAExpressions.select(favoriteProduct.count())
                .from(favoriteProduct)
                .where(favoriteProduct.product.id.eq(product.id));

        // 회원의 해당 상품 좋아요 표시 여부
        BooleanExpression isFavorite = userId != null ?
                JPAExpressions.selectOne()
                        .from(favoriteProduct)
                        .where(favoriteProduct.product.id.eq(product.id)
                                .and(favoriteProduct.user.id.eq(userId)))
                        .exists()
                : Expressions.asBoolean(false);

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
                        isFavorite.as("favorite"),
                        ExpressionUtils.as(favoriteCnt, "favoriteCnt"),
                        rentalRecord.expectedReturnDate
                ))
                .from(product)
                .leftJoin(productImage)
                .on(product.id.eq(productImage.product.id).
                        and(productImage.thumbnail.eq("Y")))
                .leftJoin(rentalRecord)
                .on(product.id.eq(rentalRecord.product.id))
                .where(builder)
                .orderBy(product.updatedAt.desc())
                .fetch();
    }

    @Override
    public ProductDetailResponseDto findProductDetail(Long productId) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        QRentalRecord rentalRecord = QRentalRecord.rentalRecord;

        // 기본 Product 정보 가져오기
        ProductDetailResponseDto productDetail = queryFactory
                .select(Projections.fields(
                        ProductDetailResponseDto.class,
                        product.id.as("productId"),
                        Projections.fields(
                                CategoryProductResponseDto.class,
                                product.category.id.as("categoryId"),
                                product.category.name.as("categoryName")
                        ).as("category"),
                        product.title.as("title"),
                        product.description.as("description"),
                        product.rentalStatus.stringValue().as("rentalStatus"),
                        product.dayPrice.as("dayPrice"),
                        product.weekPrice.as("weekPrice"),
                        product.viewCount.as("viewCount"),
                        product.updatedAt.as("updatedAt"),
                        Projections.fields(
                                ProductSellerResponseDto.class,
                                product.seller.id.as("sellerId"),
                                product.seller.nickname.as("sellerNickname"),
                                product.seller.imageUrl.as("sellerImageUrl")
                        ).as("seller")
                ))
                .from(product)
                .where(product.id.eq(productId))
                .fetchOne();

        // expectedReturnDate 가져오기
        LocalDate expectedReturnDate = queryFactory
                .select(rentalRecord.expectedReturnDate)
                .from(rentalRecord)
                .where(rentalRecord.product.id.eq(productId)
                        .and(product.rentalStatus.eq(RentalStatus.RENTED)))
                .fetchOne();

        // 이미지 리스트 가져오기
        List<ProductImageResponseDto> imageDtos = queryFactory
                .select(Projections.constructor(
                        ProductImageResponseDto.class,
                        productImage.id,
                        productImage.imageUrl,
                        productImage.thumbnail
                ))
                .from(productImage)
                .where(productImage.product.id.eq(productId))
                .fetch();

        // 4. 추가 데이터 설정
        if (productDetail != null) {
            productDetail.setExpectedReturnDate(expectedReturnDate); // 반납 예정일 설정
            productDetail.setProductImages(imageDtos != null ? imageDtos : new ArrayList<>()); // 이미지 리스트 설정
        }

        return productDetail;
    }


}
