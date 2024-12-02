package com.team01.billage.chatting.service;

import com.team01.billage.chatting.dao.ChatRoomWithLastChat;
import com.team01.billage.chatting.dao.CheckValidChatroomDao;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.dto.ChatResponseDto;
import com.team01.billage.chatting.dto.ChatroomResponseDto;
import com.team01.billage.chatting.dto.CheckValidChatroomRequestDto;
import com.team01.billage.chatting.dto.CheckValidChatroomResponseDto;
import com.team01.billage.chatting.enums.ChatType;
import com.team01.billage.chatting.repository.ChatRoomQueryDSL;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
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
    private final ChatRoomQueryDSL chatRoomQueryDsl;
    private final ChatService chatService;

    public Users determineUser(String username) {
        try {
            Long userId = Long.parseLong(username);
            return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        }
    }

    public ChatRoom isChatroomExists(Long chatroomId) {
        return chatRoomRepository.findById(chatroomId).orElseThrow(() ->
                new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
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

    public CheckValidChatroomResponseDto checkValidChatroom(CheckValidChatroomRequestDto checkValidChatroomDto) {
        Optional<ChatRoom> chatroomOpt = chatRoomRepository.checkChatroomIsExist(
                checkValidChatroomDto.getSellerId(),
                checkValidChatroomDto.getBuyerId(),
                checkValidChatroomDto.getProductId()
        );
        return chatroomOpt.map(chatRoom -> new CheckValidChatroomResponseDto(chatRoom.getId()))
                .orElseGet(() -> createChatRoom(checkValidChatroomDto));
    }

    public List<ChatResponseDto> getChatsInChatroom(Long chatroomId, Long userId, int page, Long lastChatId) {
        ChatRoom chatroom = chatRoomRepository.findById(chatroomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        if (!(userId.equals(chatroom.getSeller().getId()) || userId.equals(chatroom.getBuyer().getId()))) {
            throw new CustomException(ErrorCode.CHATROOM_ACCESS_FORBIDDEN);
        }

        return chatService.getPagenatedChat(chatroomId, lastChatId, userId, page);
    }

    public CheckValidChatroomResponseDto createChatRoom(CheckValidChatroomDao checkValidChatroomDao) {
        Users buyer = checkValidChatroomDao.getBuyer();
        Users seller = userRepository.findById(checkValidChatroomDao.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(checkValidChatroomDao.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

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

    public CheckValidChatroomResponseDto createChatRoom(CheckValidChatroomRequestDto checkValidChatroomRequestDto) {
        Users buyer = userRepository.findById(checkValidChatroomRequestDto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Users seller = userRepository.findById(checkValidChatroomRequestDto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findById(checkValidChatroomRequestDto.getProductId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

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

    public void exitFromChatRoom(Long chatroomId, Long userId) {
        ChatRoom chatroom = isChatroomExists(chatroomId);

        if (Objects.equals(chatroom.getBuyer().getId(), userId)) {
            chatroom.setBuyerExitAt();
        } else if (Objects.equals(chatroom.getSeller().getId(), userId)) {
            chatroom.setSellerExitAt();
        } else {
            throw new CustomException(ErrorCode.CHATROOM_ACCESS_FORBIDDEN);
        }
    }
}
