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
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatroomRepository;
    private final ChatRoomQueryDSL chatroomQueryDsl;
    private final ChatService chatService;
    private final ChatRedisService chatRedisService;
    private final ChatSocketService chatSocketService;

    public ChatRoom isChatroomExists(Long chatroomId) {
        return chatroomRepository.findById(chatroomId).orElseThrow(() ->
                new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
    }

    public List<ChatroomResponseDto> getAllChatroomsWithDSL(ChatType type, int page, Long userId, Long productId) {
        Pageable pageable = PageRequest.of(page, 20);
        List<ChatRoomWithLastChat> results = chatroomQueryDsl.getChatrooms(type, userId, productId, pageable);

        return results.stream()
                .map(result -> {
                    Long chatroomId = result.getChatRoom().getId();
                    String unreadKey = getUnreadChatKey(chatroomId, userId);
                    Long unreadCount = chatRedisService.getUnreadChatCount(unreadKey);
                    return new ChatroomResponseDto(
                            result.getChatRoom(),
                            result.getLastChat(),
                            userId,
                            unreadCount
                    );
                })
                .toList();
    }

    private String getUnreadChatKey(Long chatroomId, Long userId) {
        Set<String> keys = chatRedisService.getKeysByPattern(chatroomId.toString() + "_*", 2);
        return keys.stream().filter((key) ->
                Long.parseLong(key.split("_")[1]) == userId
        ).findFirst().orElse(null);
    }

    public CheckValidChatroomResponseDto checkValidChatroom(CheckValidChatroomRequestDto checkValidChatroomDto, Long userId) {
        Optional<ChatRoom> chatroomOpt = chatroomRepository.checkChatroomIsExist(
                checkValidChatroomDto.getSellerId(),
                checkValidChatroomDto.getBuyerId(),
                checkValidChatroomDto.getProductId()
        );
        return chatroomOpt.map(chatroom -> {
                    if (userId.equals(checkValidChatroomDto.getSellerId())) {
                        if (chatroom.getSellerExitAt() != null) {
                            chatroom.setSellerJoinAt();
                        }
                    } else {
                        if (chatroom.getBuyerExitAt() != null) {
                            chatroom.setBuyerJoinAt();
                        }
                    }
                    return new CheckValidChatroomResponseDto(chatroom.getId());
                })
                .orElseGet(() -> createChatRoom(checkValidChatroomDto));
    }

    public List<ChatResponseDto> getChatsInChatroom(Long chatroomId, Long userId, int page, Long lastChatId) {
        ChatRoom chatroom = chatroomRepository.findById(chatroomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        if (!(userId.equals(chatroom.getSeller().getId()) || userId.equals(chatroom.getBuyer().getId()))) {
            throw new CustomException(ErrorCode.CHATROOM_ACCESS_FORBIDDEN);
        }

        String unreadKey = getUnreadChatKey(chatroomId, userId);
        Long unreadCount = chatRedisService.getUnreadChatCount(unreadKey);

        chatRedisService.resetUnreadChatCount(unreadKey);
        chatSocketService.sendUnreadChatCount(userId, -unreadCount.intValue());

        return chatService.getPagenatedChat(chatroomId, lastChatId, userId, page);
    }

    public CheckValidChatroomResponseDto createChatRoom(CheckValidChatroomDao checkValidChatroomDao) {
        Users buyer = checkValidChatroomDao.getBuyer();
        Users seller = userRepository.findById(checkValidChatroomDao.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findByIdAndSellerId(checkValidChatroomDao.getProductId(), checkValidChatroomDao.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ChatRoom chatroom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
        chatroomRepository.save(chatroom);

        return new CheckValidChatroomResponseDto(chatroom.getId());
    }

    public CheckValidChatroomResponseDto createChatRoom(CheckValidChatroomRequestDto checkValidChatroomRequestDto) {
        Users buyer = userRepository.findById(checkValidChatroomRequestDto.getBuyerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Users seller = userRepository.findById(checkValidChatroomRequestDto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Product product = productRepository.findByIdAndSellerId(checkValidChatroomRequestDto.getProductId(), checkValidChatroomRequestDto.getSellerId()).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ChatRoom chatroom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();
        chatroomRepository.save(chatroom);

        chatRedisService.setUnreadChatCount(chatroom.getId() + "_" + buyer.getId());
        chatRedisService.setUnreadChatCount(chatroom.getId() + "_" + seller.getId());

        return new CheckValidChatroomResponseDto(chatroom.getId());
    }

    public void exitFromChatRoom(Long chatroomId, Long userId) {
        ChatRoom chatroom = isChatroomExists(chatroomId);

        if (userId.equals(chatroom.getBuyer().getId())) {
            chatroom.setBuyerExitAt();
        } else if (userId.equals(chatroom.getSeller().getId())) {
            chatroom.setSellerExitAt();
        } else {
            throw new CustomException(ErrorCode.CHATROOM_ACCESS_FORBIDDEN);
        }
    }
}
