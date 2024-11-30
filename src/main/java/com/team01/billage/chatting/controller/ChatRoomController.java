package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestUser;
import com.team01.billage.chatting.dto.*;
import com.team01.billage.chatting.enums.ChatType;
import com.team01.billage.chatting.exception.ChatRoomNotFoundException;
import com.team01.billage.chatting.exception.NotInChatRoomException;
import com.team01.billage.chatting.repository.TestUserRepository;
import com.team01.billage.chatting.service.ChatRoomService;
import com.team01.billage.chatting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    // 추후 토큰 헤더는 SecurityContext에서 가져오도록 수정
    // 일단은 더미데이터로 token에 id값을 받아와서 테스트
    private final TestUserRepository testUserRepository;

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 채팅방들 불러오기
    // 요청 전 type이 pr일 때 productId가 존재하는지 파악 필요
    @GetMapping("/api/chatroom")
    public ResponseEntity<Object> getAllChatRooms(
            @RequestParam(value = "type", required = false) ChatType type,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "page", required = false) int page,
            @RequestHeader("token") String token) {
        Long userId = Long.parseLong(token);
//        List<ChatroomResponseDto> responseDto = chatRoomService.getAllChatRooms(type, userId);
        List<ChatroomResponseDto> responseDto = chatRoomService.getAllChatroomsWithDSL(type, page, userId, productId);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/api/chatroom/valid")
    public ResponseEntity<Object> checkIsValidChatroom(@RequestBody CheckValidChatroomRequestDto checkValidChatroomDto) {
        Optional<ChatRoom> chatRoomOpt = chatRoomService.checkValidChatroom(checkValidChatroomDto);

        CheckValidChatroomResponseDto responseDto = chatRoomOpt.map(chatRoom -> new CheckValidChatroomResponseDto(chatRoom.getId()))
                .orElseGet(() -> chatRoomService.createChatRoom(checkValidChatroomDto));

        return ResponseEntity.ok(responseDto);
    }

    // 특정 채팅방의 이전 채팅 목록가져오기
    @GetMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<List<ChatResponseDto>> getAllChats(
            @PathVariable("chatroomId") Long chatroomId,
            @RequestParam("page") int page,
            @RequestParam(name = "lastLoadChatId", required = false, defaultValue = "" + Long.MAX_VALUE) Long lastLoadChatId,
            @RequestHeader("token") String token
    ) {
        Long userId = Long.parseLong(token);

        return chatRoomService.getChatRoom(chatroomId)
                .filter(chatroom -> Objects.equals(chatroom.getBuyer().getId(), userId) || Objects.equals(chatroom.getSeller().getId(), userId))
                .map(chatroom -> {
                    List<ChatResponseDto> chatsResponseDto = chatService.getPagenatedChat(chatroomId, lastLoadChatId, page);
                    return ResponseEntity.ok(chatsResponseDto);
                })
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @Transactional
    @PostMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<Object> markAsRead(@PathVariable("chatroomId") Long chatroomId, @RequestHeader("token") String token) {
        Long userId = Long.parseLong(token);
        TestUser user = testUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        chatService.markAsRead(chatroomId, user);
        return ResponseEntity.ok().build();
    }

    // 채팅방 생성
    // TODO: 예외 처리 필수!
    @Transactional
    @PostMapping("/api/chatroom")
    public ResponseEntity<Object> createChatRoom(Principal principal, @RequestBody CreateChatRoomDto createChatRoomDto) {
        Long buyerId = Long.parseLong(principal.getName());
        CheckValidChatroomResponseDto responseDto = chatRoomService.createChatRoom(
                new CheckValidChatroomRequestDto(
                        buyerId,
                        createChatRoomDto.getTargetUserId(),
                        createChatRoomDto.getProductId()
                )
        );

        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<Object> exitChatRoom(Principal principal, @PathVariable("chatRoomId") Long chatroomId) {
        long userId = Long.parseLong(principal.getName());
        try {
            chatRoomService.exitFromChatRoom(chatroomId, userId);
        } catch (ChatRoomNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NotInChatRoomException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }
}
