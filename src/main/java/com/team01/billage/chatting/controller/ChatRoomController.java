package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.dao.CheckValidChatroomDao;
import com.team01.billage.chatting.dto.*;
import com.team01.billage.chatting.enums.ChatType;
import com.team01.billage.chatting.service.ChatRoomService;
import com.team01.billage.chatting.service.ChatService;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.product.domain.Product;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    @GetMapping("/api/chatroom")
    public ResponseEntity<List<ChatroomResponseDto>> getAllChatRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "type", required = false, defaultValue="ALL") ChatType type,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "page", required = false, defaultValue="0") int page) {
        Users user = chatRoomService.determineUser(userDetails.getUsername());

        if (type == ChatType.PR && productId == null) {
            throw new CustomException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        List<ChatroomResponseDto> responseDto = chatRoomService.getAllChatroomsWithDSL(type, page, user.getId(), productId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/api/chatroom/valid")
    public ResponseEntity<CheckValidChatroomResponseDto> checkIsValidChatroom(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CheckValidChatroomRequestDto checkValidChatroomDto) {
        Users user = chatRoomService.determineUser(userDetails.getUsername());

        if (!(user.getId().equals(checkValidChatroomDto.getSellerId()) || user.getId().equals(checkValidChatroomDto.getBuyerId()))) {
            throw new CustomException(ErrorCode.CHATROOM_VALIDATE_FAILED);
        }

        CheckValidChatroomResponseDto responseDto = chatRoomService.checkValidChatroom(checkValidChatroomDto);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 채팅방의 이전 채팅 목록가져오기
    @GetMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<List<ChatResponseDto>> getAllChats(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(name = "chatroomId") Long chatroomId,
            @RequestParam(name = "page", defaultValue="0") int page,
            @RequestParam(name = "lastLoadChatId", required = false, defaultValue = "" + Long.MAX_VALUE) Long lastLoadChatId
    ) {
        Users user = chatRoomService.determineUser(userDetails.getUsername());

        List<ChatResponseDto> responseDto = chatRoomService.getChatsInChatroom(chatroomId, user.getId(), page, lastLoadChatId);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    @PostMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<Object> markAsRead(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = chatRoomService.determineUser(userDetails.getUsername());

        chatService.markAsRead(chatroomId, user);
        return ResponseEntity.ok().build();
    }

//    @Transactional
//    @PostMapping("/api/chatroom")
//    public ResponseEntity<Object> createChatRoom( @AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateChatRoomDto createChatroomDto) {
//        Users buyer = chatRoomService.determineUser(userDetails.getUsername());
//
//        CheckValidChatroomResponseDto responseDto = chatRoomService.createChatRoom(
//                new CheckValidChatroomDao(
//                        buyer,
//                        createChatroomDto.getTargetUserId(),
//                        createChatroomDto.getProductId()
//                )
//        );
//
//        return ResponseEntity.ok(responseDto);
//    }

    @Transactional
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<Object> exitChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "chatRoomId") Long chatroomId) {
        Users user = chatRoomService.determineUser(userDetails.getUsername());
        chatRoomService.exitFromChatRoom(chatroomId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
