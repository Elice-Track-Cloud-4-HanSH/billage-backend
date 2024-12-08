package com.team01.billage.rental_record.repository;

import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import java.util.List;

public interface CustomRentalRecordRepository {

    List<ShowRecordResponseDto> findBySellerRenting(long userId);

    List<ShowRecordResponseDto> findBySellerRecord(long userId);

    List<ShowRecordResponseDto> findByBuyerRenting(long userId);

    List<ShowRecordResponseDto> findByBuyerRecord(long userId);

    List<PurchasersResponseDto> loadPurchasersList(long userId, long productId);
}
