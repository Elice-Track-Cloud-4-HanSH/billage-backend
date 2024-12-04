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
import com.team01.billage.utils.DetermineUser;
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
    private final ChatRoomService chatroomService;
    private final ChatService chatService;
    private final DetermineUser determineUser;

    @GetMapping("/api/chatroom")
    public ResponseEntity<List<ChatroomResponseDto>> getAllChatRooms(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "type", required = false, defaultValue="ALL") ChatType type,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "page", required = false, defaultValue="0") int page) {
        Users user = determineUser.determineUser(userDetails.getUsername());

        if (type == ChatType.PR && productId == null) {
            throw new CustomException(ErrorCode.PRODUCT_ID_REQUIRED);
        }

        List<ChatroomResponseDto> responseDto = chatroomService.getAllChatroomsWithDSL(type, page, user.getId(), productId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/api/chatroom/valid")
    public ResponseEntity<CheckValidChatroomResponseDto> checkIsValidChatroom(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CheckValidChatroomRequestDto checkValidChatroomDto) {
        Users user = determineUser.determineUser(userDetails.getUsername());

        if (!(user.getId().equals(checkValidChatroomDto.getSellerId()) || user.getId().equals(checkValidChatroomDto.getBuyerId()))) {
            throw new CustomException(ErrorCode.CHATROOM_VALIDATE_FAILED);
        }

        CheckValidChatroomResponseDto responseDto = chatroomService.checkValidChatroom(checkValidChatroomDto, user.getId());
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
        Users user = determineUser.determineUser(userDetails.getUsername());

        List<ChatResponseDto> responseDto = chatroomService.getChatsInChatroom(chatroomId, user.getId(), page, lastLoadChatId);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    @PostMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<Object> markAsRead(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails.getUsername());

        chatService.markAsRead(chatroomId, user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<Object> exitChatRoom(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails.getUsername());
        chatroomService.exitFromChatRoom(chatroomId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
