package com.team01.billage.rental_record.repository;

import com.team01.billage.rental_record.dto.PurchasersResponseDto;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomRentalRecordRepository {

    List<ShowRecordResponseDto> findBySellerRenting(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId);

    List<ShowRecordResponseDto> findBySellerRecord(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId);

    List<ShowRecordResponseDto> findByBuyerRenting(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId);

    List<ShowRecordResponseDto> findByBuyerRecord(long userId, Pageable pageable,
        LocalDate lastStandard, Long lastId);

    List<PurchasersResponseDto> loadPurchasersList(long userId, long productId);
}
