package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.dto.*;
import com.team01.billage.chatting.service.ChatRedisService;
import com.team01.billage.chatting.service.ChatRoomService;
import com.team01.billage.chatting.service.ChatService;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.utils.DetermineUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatroomService;
    private final ChatService chatService;
    private final DetermineUser determineUser;
    private final ChatRedisService chatRedisService;

    private final Set<String> chatTypes = new HashSet<>(Arrays.asList("ALL", "PR", "LENT", "RENT"));

    @GetMapping("/unread-chat")
    public ResponseEntity<ChatMessage.UnreadCount> getUnreadChatCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long counts = chatRedisService.sumOfKeysValue("*_" + userDetails.getId());
        return ResponseEntity.ok(new ChatMessage.UnreadCount(counts));
    }

    @GetMapping
    public ResponseEntity<List<ChatroomResponseDto>> getAllChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "type", required = false, defaultValue="ALL") String type,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "page", required = false, defaultValue="0") int page
    ) {
        if (!chatTypes.contains(type)) {
            throw new CustomException(ErrorCode.INVALID_CHAT_TYPE);
        }

        Users user = determineUser.determineUser(userDetails);

        chatroomService.checkValidProductChatGetType(type, productId);

        List<ChatroomResponseDto> responseDto = chatroomService.getAllChatroomsWithDSL(type, page, user.getId(), productId);
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/valid")
    public ResponseEntity<CheckValidChatroomResponseDto> checkIsValidChatroom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CheckValidChatroomRequestDto checkValidChatroomDto
    ) {
        Users user = determineUser.determineUser(userDetails);

        CheckValidChatroomResponseDto responseDto = chatroomService.checkValidChatroom(checkValidChatroomDto, user);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 채팅방의 이전 채팅 목록가져오기
    @GetMapping("/{chatroomId}")
    public ResponseEntity<List<ChatResponseDto>> getAllChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "chatroomId") Long chatroomId,
            @RequestParam(name = "page", defaultValue="0") int page,
            @RequestParam(name = "lastLoadChatId", required = false, defaultValue = "" + Long.MAX_VALUE) Long lastLoadChatId
    ) {
        Users user = determineUser.determineUser(userDetails);

        List<ChatResponseDto> responseDto = chatroomService.getChatsInChatroom(chatroomId, user.getId(), page, lastLoadChatId);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    @PostMapping("/{chatroomId}")
    public ResponseEntity<Object> markAsRead(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails);

        chatService.markAsRead(chatroomId, user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{chatroomId}")
    public ResponseEntity<Object> exitChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails);

        chatroomService.exitFromChatRoom(chatroomId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
