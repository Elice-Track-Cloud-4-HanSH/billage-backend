package com.team01.billage.rental_record.service;

import com.team01.billage.rental_record.dto.StartRentalRequestDto;
import com.team01.billage.rental_record.repository.RentalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalRecordService {

    private final RentalRecordRepository rentalRecordRepository;
    //private final ChatRoomRepository chatRoomRepository;

    public void createRentalRecord(StartRentalRequestDto startRentalRequestDto, String email) {
    /*
        ChatRoom chatRoom = chatRoomRepository.findById(startRentalRequestDto.getId())
            .orElseThrow(() -> new CustomException(CHATROOM_NOT_FOUND));

        if (!chatRoom.getSeller().getEmail().equals(email)) {
            throw new CustomException(CHANGE_ACCESS_FORBIDDEN);
        }

        RentalRecord rentalRecord = RentalRecord.builder()
            .startDate(startRentalRequestDto.getStartDate())
            .expectedReturnDate(startRentalRequestDto.getExpectedRentalDate())
            .imageUrl(chatRoom.getProduct().getImageUrl())
            .title(chatRoom.getProduct().getTitle())
            .seller(chatRoom.getSeller())
            .buyer(chatRoom.getBuyer())
            .product(chatRoom.getProduct())
            .build();

        rentalRecordRepository.save(rentalRecord);*/
    }
}
