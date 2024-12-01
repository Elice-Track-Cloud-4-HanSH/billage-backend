package com.team01.billage.chatting.service;

import com.team01.billage.chatting.dao.ChatRoomWithLastChat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.dto.ChatroomResponseDto;
import com.team01.billage.chatting.dto.CheckValidChatroomRequestDto;
import com.team01.billage.chatting.dto.CheckValidChatroomResponseDto;
import com.team01.billage.chatting.enums.ChatType;
import com.team01.billage.chatting.exception.ChatRoomNotFoundException;
import com.team01.billage.chatting.exception.NotInChatRoomException;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomQueryDSL;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.product.domain.Product;
import com.team01.billage.product.repository.ProductRepository;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomQueryDSL chatRoomQueryDsl;

    public ChatRoom isChatroomExists(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId).orElseThrow(() -> new ChatRoomNotFoundException("해당 id를 가진 채팅방은 없습니다."));
    }

    public List<ChatroomResponseDto> getAllChatroomsWithDSL(ChatType type, int page, Long userId, Long productId) {
        Pageable pageable = PageRequest.of(page, 20);
        List<ChatRoomWithLastChat> results = chatRoomQueryDsl.getChatrooms(type, userId, productId, pageable);

        return results.stream()
                .map(result -> new ChatroomResponseDto(
                        result.getChatRoom(),
                        result.getLastChat()
                ))
                .toList();
    }

    public Optional<ChatRoom> getChatRoom(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId);
    }

    public Optional<ChatRoom> checkValidChatroom(CheckValidChatroomRequestDto checkValidChatroomDto) {
        return chatRoomRepository.checkChatroomIsExist(
                checkValidChatroomDto.getSellerId(),
                checkValidChatroomDto.getBuyerId(),
                checkValidChatroomDto.getProductId()
        );
    }

    public CheckValidChatroomResponseDto createChatRoom(CheckValidChatroomRequestDto checkValidChatroomDto) {
        Users buyer = userRepository.findById(checkValidChatroomDto.getBuyerId()).orElseThrow(() -> new RuntimeException("해당 ID의 유저가 존재하지 않습니다."));
        Users seller = userRepository.findById(checkValidChatroomDto.getSellerId()).orElseThrow(() -> new RuntimeException("해당 ID의 유저가 존재하지 않습니다."));
        Product product = productRepository.findById(checkValidChatroomDto.getProductId()).orElseThrow(() -> new RuntimeException("제품이 존재하지 않습니다."));

        ChatRoom chatRoom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
        chatRoomRepository.save(chatRoom);
        System.out.println(chatRoom.getId());

        return new CheckValidChatroomResponseDto(chatRoom.getId());
    }

    public void exitFromChatRoom(Long chatroomId, Long userId) throws Exception {
        ChatRoom chatroom = isChatroomExists(chatroomId);

        if (Objects.equals(chatroom.getBuyer().getId(), userId)) {
            chatroom.setBuyerExitAt();
        } else if (Objects.equals(chatroom.getSeller().getId(), userId)) {
            chatroom.setSellerExitAt();
        } else {
            throw new NotInChatRoomException("해당 채팅방에 있는 유저가 아닙니다.");
        }
    }
}
