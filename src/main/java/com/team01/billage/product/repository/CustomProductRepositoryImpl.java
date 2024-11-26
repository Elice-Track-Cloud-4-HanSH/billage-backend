package com.team01.billage.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.RentalStatus;
import com.team01.billage.user.domain.QUsers;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findAllOnSale(String email) {
        QProduct product = QProduct.product;
        QUsers seller = QUsers.users;

        return queryFactory
            .selectFrom(product)
            .join(product.seller, seller)
            .where(seller.email.eq(email)
                .and(product.rentalStatus.eq(RentalStatus.AVAILABLE))
                .and(product.deletedAt.isNull()))
            .orderBy(product.updatedAt.desc())
            .fetch();
    }
}
