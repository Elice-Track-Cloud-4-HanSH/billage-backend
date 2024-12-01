package com.team01.billage.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.product.dto.OnSaleResponseDto;
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
}
