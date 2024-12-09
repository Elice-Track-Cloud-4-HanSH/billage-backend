package com.team01.billage.chatting.service;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.dto.ChatResponseDto;
import com.team01.billage.chatting.dto.querydsl.ChatWithSenderDTO;
import com.team01.billage.chatting.repository.ChatQueryDSL;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.user.domain.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatQueryDSL chatQueryDsl;

    public List<ChatResponseDto> getPagenatedChat(Long chatroomId, Long chatId, Long userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        List<ChatWithSenderDTO> chats = chatQueryDsl.getPagenatedChatsInChatroom(chatroomId, chatId, userId, pageable);

        return chats.stream()
                .map(chat -> new ChatResponseDto(
                        chat.getChatId(),
                        chat.getSender(),
                        chat.getMessage(),
                        chat.isRead()
                ))
                .toList();
    }

    public void markAsRead(Long chatroomId, Users user) {
        Chat lastReadChat = chatRepository.getLastReadChat(chatroomId, user.getId())
                .orElseGet(() -> chatRepository.getFirstChat(chatroomId, user.getId()).orElse(null));

        if (lastReadChat != null) {
            chatRepository.markAsRead(chatroomId, user.getId(), lastReadChat.getCreatedAt());
        }
    }
}
