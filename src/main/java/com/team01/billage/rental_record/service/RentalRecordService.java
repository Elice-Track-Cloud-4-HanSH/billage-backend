package com.team01.billage.rental_record.service;

import static com.team01.billage.exception.ErrorCode.CHANGE_ACCESS_FORBIDDEN;
import static com.team01.billage.exception.ErrorCode.CHATROOM_NOT_FOUND;
import static com.team01.billage.exception.ErrorCode.PRODUCT_ALREADY_RETURNED;
import static com.team01.billage.exception.ErrorCode.RENTAL_RECORD_NOT_FOUND;

import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.enums.RentalStatus;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.rental_record.domain.RecordType;
import com.team01.billage.rental_record.domain.RentalRecord;
import com.team01.billage.rental_record.dto.PurchasersResponseDto;
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
    public void createRentalRecord(StartRentalRequestDto startRentalRequestDto, long userId) {

        ChatRoom chatRoom = chatRoomRepository.findById(startRentalRequestDto.getId())
            .orElseThrow(() -> new CustomException(CHATROOM_NOT_FOUND));

        if (chatRoom.getSeller().getId() != (userId)) {
            throw new CustomException(CHANGE_ACCESS_FORBIDDEN);
        }

        Product product = chatRoom.getProduct();
        product.updateRentalStatus(RentalStatus.RENTED);

        RentalRecord rentalRecord = RentalRecord.builder()
            .startDate(startRentalRequestDto.getStartDate())
            .expectedReturnDate(startRentalRequestDto.getExpectedReturnDate())
            .seller(chatRoom.getSeller())
            .buyer(chatRoom.getBuyer())
            .product(product)
            .build();

        productRepository.save(product);
        rentalRecordRepository.save(rentalRecord);
    }

    public List<PurchasersResponseDto> readPurchasers(long userId, long productId) {

        return rentalRecordRepository.loadPurchasersList(userId, productId);
    }

    public List<ShowRecordResponseDto> readRentalRecords(String type, long userId) {

        RecordType recordType = RecordType.fromDescription(type);

        return switch (recordType) {
            case SELLER_RENTING -> rentalRecordRepository.findBySellerRenting(userId);
            case SELLER_RECORD -> rentalRecordRepository.findBySellerRecord(userId);
            case BUYER_RENTING -> rentalRecordRepository.findByBuyerRenting(userId);
            case BUYER_RECORD -> rentalRecordRepository.findByBuyerRecord(userId);
        };
    }

    @Transactional
    public void updateRentalRecord(long rentalRecordId, long userId) {

        RentalRecord rentalRecord = rentalRecordRepository.findById(rentalRecordId)
            .orElseThrow(() -> new CustomException(RENTAL_RECORD_NOT_FOUND));

        Product product = rentalRecord.getProduct();

        if (rentalRecord.getSeller().getId() != (userId)) {
            throw new CustomException(CHANGE_ACCESS_FORBIDDEN);
        }

        if (rentalRecord.getReturnDate() != null) {
            throw new CustomException(PRODUCT_ALREADY_RETURNED);
        }

        product.updateRentalStatus(RentalStatus.AVAILABLE);
        rentalRecord.productReturn();

        productRepository.save(product);
        rentalRecordRepository.save(rentalRecord);
    }
}
