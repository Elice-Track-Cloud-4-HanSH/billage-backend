package com.team01.billage.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product.dto.OnSaleResponseDto;
import com.team01.billage.product.dto.ProductResponseDto;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.user.domain.QUsers;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OnSaleResponseDto> findAllOnSale(String email, LocalDateTime lastTime,
        Pageable pageable) {
        QProduct product = QProduct.product;
        QUsers seller = QUsers.users;
        QProductImage productImage = QProductImage.productImage;

        return queryFactory
            .select(Projections.constructor(
                OnSaleResponseDto.class,
                product.id,
                productImage.imageUrl,
                product.title,
                Expressions.dateTimeTemplate(LocalDate.class, "COALESCE({0}, {1})",
                    product.updatedAt,
                    product.createdAt)
            ))
            .from(product)
            .join(product.seller, seller)
            .leftJoin(productImage)
            .on(productImage.product.eq(product)
                .and(productImage.thumbnail.eq("Y")))
            .where(seller.email.eq(email)
                .and(product.rentalStatus.eq(RentalStatus.AVAILABLE))
                .and(product.deletedAt.isNull())
                .and(Expressions.booleanTemplate("COALESCE({0}, {1}) < {2}",
                    product.updatedAt, product.createdAt, lastTime))
            )
            .orderBy(Expressions.dateTimeTemplate(LocalDateTime.class, "COALESCE({0}, {1})",
                product.updatedAt,
                product.createdAt).desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    @Override
    public List<ProductResponseDto> findAllProductsByCategoryId(Long categoryId) {
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;

        // 동적 조건 처리
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.deletedAt.isNull());

        if (categoryId != 1L) {
            builder.and(product.category.id.eq(categoryId));
        }

        return queryFactory
            .select(Projections.constructor(
                ProductResponseDto.class,
                product.id,
                product.title,
                product.updatedAt,
                product.dayPrice,
                product.weekPrice,
                product.viewCount,
                productImage.imageUrl
            ))
            .from(product)
            .leftJoin(productImage)
            .on(product.id.eq(productImage.product.id).
                and(productImage.thumbnail.eq("Y")))
            .where(builder)
            .orderBy(product.updatedAt.desc())
            .fetch();
    }
}
