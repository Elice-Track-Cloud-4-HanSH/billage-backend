package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestProduct;
import com.team01.billage.chatting.domain.TestUser;
import com.team01.billage.chatting.dto.ChatroomResponseDto;
import com.team01.billage.chatting.dto.ChatsResponseDto;
import com.team01.billage.chatting.dto.CreateChatRoomDto;
import com.team01.billage.chatting.enums.ChatType;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.chatting.repository.TestProductRepository;
import com.team01.billage.chatting.repository.TestUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    // 추후 토큰 헤더는 SecurityContext에서 가져오도록 수정
    // 일단은 더미데이터로 token에 id값을 받아와서 테스트

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final TestUserRepository testUserRepository;
    private final TestProductRepository testProductRepository;

    // 채팅방들 불러오기
    @GetMapping("/api/chatroom")
    public ResponseEntity<Object> getAllChatRooms(@RequestParam(value = "type") ChatType type, @RequestHeader("token") String token) {
        Long userId = Long.parseLong(token);
        Pageable pageable = PageRequest.of(0, 20);
        List<ChatRoom> results = switch (type) {
            case ALL -> chatRoomRepository.getAllChatroom(userId, pageable);
            case LENT -> chatRoomRepository.getToSellChatroom(userId, pageable);
            case RENT -> chatRoomRepository.getToBuyChatroom(userId, pageable);
        };

        List<ChatroomResponseDto> responseDto = results.stream()
                .map(result -> new ChatroomResponseDto(
                        result.getId(),
                        result.getBuyer(),
                        result.getSeller(),
                        result.getChats().get(0)
                ))
                .toList();
        return ResponseEntity.ok(responseDto);
    }

    // 특정 채팅방의 이전 채팅 목록가져오기
    @GetMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<List<ChatsResponseDto>> getAllChats(
            @PathVariable("chatroomId") Long chatroomId,
            @RequestParam("page") int page,
            @RequestHeader("token") String token
    ) {
        Long userId = Long.parseLong(token);
        ChatRoom chatroom = chatRoomRepository.findById(chatroomId).orElse(null);
        if (chatroom == null) {
            return ResponseEntity.notFound().build();
        }
        if (!Objects.equals(chatroom.getBuyer().getId(), userId) && !Objects.equals(chatroom.getSeller().getId(), userId)) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, 50);
        List<Chat> chats = chatRepository.getAllChatsInChatroom(chatroomId, pageable);

        List<ChatsResponseDto> chatsResponse = chats.stream()
                .map(chat -> new ChatsResponseDto(
                        chat.getSender().getId(),
                        chat.getMessage(),
                        chat.isRead(),
                        chat.getCreatedAt()))
                .toList();
        return ResponseEntity.ok(chatsResponse);
    }

    @Transactional
    @PostMapping("/api/chatroom/{chatroomId}")
    public ResponseEntity<Object> markAsRead(@PathVariable("chatroomId") Long chatroomId, @RequestHeader("token") String token) {
        Long userId = Long.parseLong(token);
        TestUser user = testUserRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Pageable getOne = PageRequest.of(0, 1);

        // 1. chatroomId와 연관된 chat들을 찾고
        // 2. 내가 발신자가 아닌 것들에 대해
        // 3. 가장 최근의 read된 것의 날짜 및 시각을 바탕으로
        // 4. 지금까지 온 채팅들 중
        // 5. isRead가 false인 것들을 true로 변환
        try {
            Chat lastReadChat = chatRepository.getLastReadChat(chatroomId, userId, getOne).get(0);
            chatRepository.markAsRead(chatroomId, userId, lastReadChat.getCreatedAt());
        } catch (IndexOutOfBoundsException e) {
            try {
                Chat firstChat = chatRepository.getFirstChat(chatroomId, userId, getOne).get(0);
                chatRepository.markAsRead(chatroomId, userId, firstChat.getCreatedAt());
            } catch (IndexOutOfBoundsException e2) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }

        return ResponseEntity.ok().build();
    }

    // 채팅방 생성
    // TODO: 예외 처리 필수!
    @Transactional
    @PostMapping("/api/chatroom")
    public ResponseEntity<Object> createChatRoom(Principal principal, @RequestBody CreateChatRoomDto createChatRoomDto) {
        Long userId = Long.parseLong(principal.getName());
        TestUser buyer = testUserRepository.findById(userId).orElse(null);
        TestUser seller = testUserRepository.findById(createChatRoomDto.getTargetUserId()).orElse(null);

        TestProduct product = testProductRepository.findById(createChatRoomDto.getProductId()).orElse(null);

        ChatRoom chatRoom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        chatRoomRepository.save(chatRoom);

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/api/chatroom/{chatRoomId}")
    public ResponseEntity<Object> exitChatRoom(Principal principal, @PathVariable("chatRoomId") Long chatRoomId) {
        long userId = Long.parseLong(principal.getName());
        ChatRoom chatroom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (chatroom == null) {
            return ResponseEntity.notFound().build();
        }

        if (chatroom.getBuyer().getId() == userId) {
            chatroom.setBuyerExitAt();
        } else if (chatroom.getSeller().getId() == userId) {
            chatroom.setSellerExitAt();
        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
