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

    public ChatroomResponseDto(ChatRoom chatroom, Chat lastChat) {
        this.chatroomId = chatroom.getId();
        this.buyer = new CustomChatResponseUser(chatroom.getBuyer());
        this.seller = new CustomChatResponseUser(chatroom.getSeller());
        this.lastChat = new CustomChatResponse(lastChat);
        this.product = new CustomChatResponseProduct(chatroom.getProduct());
    }
}


