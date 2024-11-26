package com.team01.billage.chatting.service;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestUser;
import com.team01.billage.chatting.dto.ChatsResponseDto;
import com.team01.billage.chatting.repository.ChatRepository;
import com.team01.billage.chatting.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public List<ChatsResponseDto> getPagenatedChat(Long chatroomId, int page) {
        Pageable pageable = PageRequest.of(page, 50);
        List<Chat> chats = chatRepository.getPagenatedChatsInChatroom(chatroomId, pageable);

        return chats.stream()
                .map(chat -> new ChatsResponseDto(
                        chat.getSender(),
                        chat.getMessage(),
                        chat.isRead(),
                        chat.getCreatedAt()))
                .toList();
    }

    public void markAsRead(Long chatroomId, TestUser user) {
        Pageable getOne = PageRequest.of(0, 1);
//
//        Optional<Chat> lastReadChats = chatRepository.getLastReadChat(chatroomId, user.getId());
//        Chat lastReadChat = lastReadChats.isEmpty() ? getFirstChat(chatroomId, user.getId()) : lastReadChats.get(0);

        Chat lastReadChat = chatRepository.getLastReadChat(chatroomId, user.getId())
                .orElseGet(() -> chatRepository.getFirstChat(chatroomId, user.getId()).orElse(null));

        if (lastReadChat != null) {
            chatRepository.markAsRead(chatroomId, user.getId(), lastReadChat.getCreatedAt());
        }
    }
}
