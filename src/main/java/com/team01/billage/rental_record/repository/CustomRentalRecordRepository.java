package com.team01.billage.rental_record.repository;

import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import java.util.List;

public interface CustomRentalRecordRepository {

    List<ShowRecordResponseDto> findBySellerRenting(String emaill);

    List<ShowRecordResponseDto> findBySellerRecord(String emaill);

    List<ShowRecordResponseDto> findByBuyerRenting(String emaill);

    List<ShowRecordResponseDto> findByBuyerRecord(String emaill);
}
