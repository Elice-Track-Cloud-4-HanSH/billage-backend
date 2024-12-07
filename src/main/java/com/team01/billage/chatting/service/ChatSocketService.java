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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSocketService {
    private final ChatRoomRepository chatroomRepository;
    private final ChatRepository chatRepository;
    private final ChatRedisService chatRedisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatResponseDto insertChat(Long chatroomId, Users sender, ChatMessage.Chatting message) {
        ChatRoom chatroom = chatroomRepository.findById(chatroomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));

        Chat chat = Chat.builder()
                .chatRoom(chatroom)
                .sender(sender)
                .message(message.getMessage())
                .build();

        chatRepository.save(chat);

        Long targetId = getTargetId(chatroom, sender);
        sendUnreadChatCount(targetId, 1);
        setToJoin(chatroom, sender);

        chatRedisService.increaseUnreadChatCount(chatroomId, targetId);

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
        ChatRoom chatroom = chat.getChatRoom();
        Long chatroomId = chatroom.getId();

        Long targetId = getTargetId(chatroom, chat.getSender());

        sendUnreadChatCount(targetId, -1);
        chatRedisService.resetUnreadChatCount(chatroomId, targetId);
    }

    private Long getTargetId(ChatRoom chatroom, Users sender) {
        Long buyerId = chatroom.getBuyer().getId();
        Long sellerId = chatroom.getSeller().getId();

        return buyerId.equals(sender.getId()) ? sellerId : buyerId;
    }

    @Async
    public void sendUnreadChatCount(Long targetId, int value) {
        String type = value < 0 ? "-" : "+";
        sendUnreadChatCount(targetId, type, value);
    }

    @Async
    public void sendUnreadChatCount(Long targetId, String type, int value) {
        ChatMessage.UnreadCountDelta deltaData =
                new ChatMessage.UnreadCountDelta(type, Math.abs(value));
        messagingTemplate.convertAndSend("/sub/chat/unread/" + targetId, deltaData);
    }
}
