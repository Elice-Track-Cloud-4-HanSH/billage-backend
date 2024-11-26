package com.team01.billage.rental_record.service;

import static com.team01.billage.exception.ErrorCode.CHANGE_ACCESS_FORBIDDEN;
import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;

import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.rental_record.domain.RentalRecord;
import com.team01.billage.rental_record.dto.ShowRecordResponseDto;
import com.team01.billage.rental_record.dto.StartRentalRequestDto;
import com.team01.billage.rental_record.repository.RentalRecordRepository;
import com.team01.billage.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RentalRecordService {

    private final RentalRecordRepository rentalRecordRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createRentalRecord(StartRentalRequestDto startRentalRequestDto, String email) {
        /*
        ChatRoom chatRoom = chatRoomRepository.findById(startRentalRequestDto.getId())
            .orElseThrow(() -> new CustomException(CHATROOM_NOT_FOUND));

        if (!chatRoom.getSeller().getEmail().equals(email)) {
            throw new CustomException(CHANGE_ACCESS_FORBIDDEN);
        }

        Product product = chatRoom.getProduct();
        product.updateRentalStatus(RentalStatus.RENTED);

        RentalRecord rentalRecord = RentalRecord.builder()
            .startDate(startRentalRequestDto.getStartDate())
            .expectedReturnDate(startRentalRequestDto.getExpectedRentalDate())
            //.imageUrl(product.getImageUrl())
            .title(product.getTitle())
            .seller(chatRoom.getSeller())
            .buyer(chatRoom.getBuyer())
            .product(product)
            .build();

        productRepository.save(product);
        rentalRecordRepository.save(rentalRecord);*/
    }

    public List<ShowRecordResponseDto> readRentalRecords(String type, String email) {

        return switch (type) {
            case "대여중/판매" -> rentalRecordRepository.findBySellerRenting(email);
            case "대여기록/판매" -> rentalRecordRepository.findBySellerRecord(email);
            case "대여중/구매" -> rentalRecordRepository.findByBuyerRenting(email);
            default -> rentalRecordRepository.findByBuyerRecord(email);
        };
    }

    @Transactional
    public void updateRentalRecord(long rentalRecordId, String email) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));

        if (!rentalRecord.getSeller().getEmail().equals(email)) {
            throw new CustomException(CHANGE_ACCESS_FORBIDDEN);
        }

        rentalRecord.productReturn();

        rentalRecordRepository.save(rentalRecord);
    }
}
