package com.team01.billage.rental_record.domain;

import static com.team01.billage.exception.ErrorCode.INVALID_QUERY_PARAMETER_TYPE;

import com.team01.billage.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecordType {
    SELLER_RENTING("대여중/판매"),
    SELLER_RECORD("대여내역/판매"),
    BUYER_RENTING("대여중/구매"),
    BUYER_RECORD("대여내역/구매");

    private final String description;

    public static RecordType fromDescription(String description) {
        for (RecordType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new CustomException(INVALID_QUERY_PARAMETER_TYPE);
    }
}

