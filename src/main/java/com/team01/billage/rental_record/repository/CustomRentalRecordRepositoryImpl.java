package com.team01.billage.rental_record.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.rental_record.domain.QRentalRecord;
import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import com.team01.billage.user.domain.QUsers;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class CustomRentalRecordRepositoryImpl implements CustomRentalRecordRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowRecordResponseDto> findBySellerRenting(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId) {
        return findRecords(userId, QRentalRecord.rentalRecord.seller, true, pageable, lastStandard,
            lastId);
    }

    @Override
    public List<ShowRecordResponseDto> findBySellerRecord(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId) {
        return findRecords(userId, QRentalRecord.rentalRecord.seller, false, pageable,
            lastStandard, lastId);
    }

    @Override
    public List<ShowRecordResponseDto> findByBuyerRenting(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId) {
        return findRecords(userId, QRentalRecord.rentalRecord.buyer, true, pageable, lastStandard,
            lastId);
    }

    @Override
    public List<ShowRecordResponseDto> findByBuyerRecord(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId) {
        return findRecords(userId, QRentalRecord.rentalRecord.buyer, false, pageable, lastStandard,
            lastId);
    }

    public List<ShowRecordResponseDto> findRecords(long userId, QUsers userType,
        boolean isRenting, Pageable pageable, LocalDate lastStandard, Long lastId) {

        QRentalRecord rentalRecord = QRentalRecord.rentalRecord;
        QProductImage productImage = QProductImage.productImage;
        QProduct product = QProduct.product;
        QUsers users = QUsers.users;

        BooleanExpression userCondition = userType.id.eq(userId);
        BooleanExpression rentingCondition = isRenting
            ? rentalRecord.returnDate.isNull()
            : rentalRecord.returnDate.isNotNull();
        BooleanBuilder paginationCondition = buildPaginationCondition(
            isRenting, rentalRecord, lastStandard, lastId);

        return queryFactory.select(
                Projections.constructor(
                    ShowRecordResponseDto.class,
                    rentalRecord.id,
                    rentalRecord.startDate,
                    rentalRecord.expectedReturnDate,
                    rentalRecord.returnDate,
                    rentalRecord.seller.id,
                    rentalRecord.buyer.id,
                    product.id,
                    productImage.imageUrl,
                    product.title,
                    users.id,
                    users.imageUrl,
                    users.nickname
                ))
            .from(rentalRecord)
            .join(userType == QRentalRecord.rentalRecord.seller ? rentalRecord.buyer
                    : rentalRecord.seller,
                users)
            .join(rentalRecord.product, product)
            .leftJoin(productImage)
            .on(productImage.product.eq(product)
                .and(productImage.thumbnail.eq("Y")))
            .where(
                userCondition
                    .and(rentingCondition)
                    .and(paginationCondition)
            )
            .orderBy(
                isRenting
                    ? rentalRecord.expectedReturnDate.asc()
                    : rentalRecord.returnDate.asc(),
                rentalRecord.id.asc()
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();
    }

    private BooleanBuilder buildPaginationCondition(
        boolean isRenting, QRentalRecord rentalRecord, LocalDate lastStandard, Long lastId) {

        BooleanBuilder builder = new BooleanBuilder();

        if (lastStandard != null) {
            BooleanExpression dateCondition = isRenting
                ? rentalRecord.expectedReturnDate.gt(lastStandard)
                : rentalRecord.returnDate.gt(lastStandard);

            BooleanExpression sameDateCondition = isRenting
                ? rentalRecord.expectedReturnDate.eq(lastStandard).and(rentalRecord.id.gt(lastId))
                : rentalRecord.returnDate.eq(lastStandard).and(rentalRecord.id.gt(lastId));

            builder.or(dateCondition).or(sameDateCondition);
        }

        return builder;
    }


    @Override
    public List<PurchasersResponseDto> loadPurchasersList(long userId, long productId) {

        QUsers buyer = QUsers.users;
        QChatRoom chatRoom = QChatRoom.chatRoom;

        return queryFactory.select(
                Projections.constructor(
                    PurchasersResponseDto.class,
                    chatRoom.id,
                    buyer.imageUrl,
                    buyer.nickname
                )
            ).from(chatRoom)
            .join(chatRoom.buyer, buyer)
            .where(chatRoom.seller.id.eq(userId)
                .and(chatRoom.product.id.eq(productId)))
            .fetch();
    }
}

