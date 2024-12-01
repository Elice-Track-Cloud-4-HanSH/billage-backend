package com.team01.billage.chatting.dto;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.product.domain.Product;
import com.team01.billage.user.domain.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

@Getter
class CustomChatResponseUser {
    private final Long id;
    private final String nickname;

    public CustomChatResponseUser(Users user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}

@Getter
class CustomChatResponse {
    private final String message;
    private final LocalDateTime lastSentTime;

    public CustomChatResponse(Chat lastChat) {
        this.message = lastChat.getMessage();
        this.lastSentTime = lastChat.getCreatedAt();
    }
}

@Getter
class CustomChatResponseProduct {
    private final Long id;
    private final String name;

    public CustomChatResponseProduct(Product product) {
        this.id = product.getId();
        this.name = product.getTitle();
    }
}
