package com.team01.billage.rental_record.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.product.domain.QProductImage;
import com.team01.billage.rental_record.domain.QRentalRecord;
import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import com.team01.billage.user.domain.QUsers;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomRentalRecordRepositoryImpl implements CustomRentalRecordRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ShowRecordResponseDto> findBySellerRenting(String email) {
        return findRecords(email, QRentalRecord.rentalRecord.seller, true);
    }

    @Override
    public List<ShowRecordResponseDto> findBySellerRecord(String email) {
        return findRecords(email, QRentalRecord.rentalRecord.seller, false);
    }

    @Override
    public List<ShowRecordResponseDto> findByBuyerRenting(String email) {
        return findRecords(email, QRentalRecord.rentalRecord.buyer, true);
    }

    @Override
    public List<ShowRecordResponseDto> findByBuyerRecord(String email) {
        return findRecords(email, QRentalRecord.rentalRecord.buyer, false);
    }

    private List<ShowRecordResponseDto> findRecords(String email, QUsers userType,
        boolean isRenting) {

        QRentalRecord rentalRecord = QRentalRecord.rentalRecord;
        QProductImage productImage = QProductImage.productImage;
        QProduct product = QProduct.product;
        QUsers users = QUsers.users;

        return queryFactory.select(
                Projections.constructor(
                    ShowRecordResponseDto.class,
                    rentalRecord.id,
                    rentalRecord.startDate,
                    rentalRecord.expectedReturnDate,
                    rentalRecord.returnDate,
                    product.id,
                    productImage.imageUrl,
                    product.title,
                    users.imageUrl,
                    users.nickname
                ))
            .from(rentalRecord)
            .join(userType == QRentalRecord.rentalRecord.seller ? rentalRecord.seller
                    : rentalRecord.buyer,
                users)
            .join(rentalRecord.product, product)
            .leftJoin(productImage)
            .on(productImage.product.eq(product)
                .and(productImage.thumbnail.eq("Y")))
            .where(
                userType.email.eq(email)
                    .and(isRenting ? rentalRecord.returnDate.isNull()
                        : rentalRecord.returnDate.isNotNull())
            )
            .orderBy(rentalRecord.createdAt.desc())
            //.limit()
            .fetch();
    }

    @Override
    public List<PurchasersResponseDto> loadPurchasersList(String email, long productId) {

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
            .where(chatRoom.seller.email.eq(email)
                .and(chatRoom.product.id.eq(productId)))
            .fetch();
    }
}

