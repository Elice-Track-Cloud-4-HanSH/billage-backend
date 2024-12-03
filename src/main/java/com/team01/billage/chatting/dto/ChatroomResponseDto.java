package com.team01.billage.chatting.dto;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.dto.object.CustomChatResponse;
import com.team01.billage.chatting.dto.object.CustomChatResponseProduct;
import com.team01.billage.chatting.dto.object.CustomChatResponseUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatroomResponseDto {
    private Long chatroomId;
    private CustomChatResponseUser buyer;
    private CustomChatResponseUser seller;
    private CustomChatResponse lastChat;
    private CustomChatResponseProduct product;
    private CustomChatResponseUser opponent;
    private Long unreadCount;

    public ChatroomResponseDto(ChatRoom chatroom, Chat lastChat, Long userId, Long unreadCount) {
        this.chatroomId = chatroom.getId();
        this.buyer = new CustomChatResponseUser(chatroom.getBuyer());
        this.seller = new CustomChatResponseUser(chatroom.getSeller());
        this.lastChat = new CustomChatResponse(lastChat);
        this.product = new CustomChatResponseProduct(chatroom.getProduct());
        this.opponent = userId.equals(chatroom.getBuyer().getId())
                ? new CustomChatResponseUser(chatroom.getSeller())
                : new CustomChatResponseUser(chatroom.getBuyer());
        this.unreadCount = unreadCount;
    }
}


