package com.team01.billage.chatting.service;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.dto.ChatMessage;
import com.team01.billage.chatting.dto.ChatResponseDto;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatSocketService {
    private final ChatRoomRepository chatroomRepository;
    private final ChatRepository chatRepository;
    private final ChatRedisService chatRedisService;

    @Transactional
    public ChatResponseDto insertChat(Long chatroomId, Users sender, ChatMessage message) {
        ChatRoom chatroom = chatroomRepository.findById(chatroomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        Chat chat = Chat.builder()
                .chatRoom(chatroom)
                .sender(sender)
                .message(message.getMessage())
                .build();

        chatRepository.save(chat);

        setToJoin(chatroom, sender);

        chatRedisService.increaseUnreadChatCount(chatroomId, sender.getId());

        return chat.toChatResponse(sender.getId());
    }

    @Transactional
    public void setToJoin(ChatRoom chatroom, Users sender) {
        if (chatroom.getBuyer().getId().equals(sender.getId())) {
            if (chatroom.getSellerExitAt() != null) {
                chatroom.setSellerJoinAt();
            }
        } else {
            if (chatroom.getBuyerExitAt() != null) {
                chatroom.setBuyerJoinAt();
            }
        }
    }

    @Transactional
    public void markAsRead(Long chatId) {
        chatRepository.markAsRead(chatId);

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new CustomException(ErrorCode.CHAT_NOT_FOUND));
        Long chatroomId = chat.getChatRoom().getId();
        Long senderId = chat.getSender().getId();
        chatRedisService.resetUnreadChatCount(chatroomId, senderId);
    }


}
