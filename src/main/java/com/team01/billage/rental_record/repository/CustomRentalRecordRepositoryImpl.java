package com.team01.billage.rental_record.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.rental_record.domain.QRentalRecord;
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

        return queryFactory.select(
                Projections.constructor(
                    ShowRecordResponseDto.class,
                    rentalRecord.id,
                    rentalRecord.startDate,
                    rentalRecord.expectedReturnDate,
                    rentalRecord.returnDate,
                    rentalRecord.product.id,
                    //rentalRecord.product.imageUrl,
                    rentalRecord.product.title,
                    userType.id,
                    userType.imageUrl,
                    userType.nickname
                ))
            .from(rentalRecord)
            .join(userType.equals(QRentalRecord.rentalRecord.seller) ? rentalRecord.seller
                    : rentalRecord.buyer,
                userType)
            .where(
                userType.email.eq(email)
                    .and(isRenting ? rentalRecord.returnDate.isNull()
                        : rentalRecord.returnDate.isNotNull())
            )
            .fetch();
    }
}

