package com.team01.billage.chatting.dto;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestProduct;
import com.team01.billage.chatting.domain.TestUser;
import com.team01.billage.product.domain.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatroomResponseDto {
    private Long chatroomId;
    private TestUser buyer;
    private TestUser seller;
    private CustomChatResponse lastChat;
    private CustomProductResponse product;

    public ChatroomResponseDto(ChatRoom chatroom, Chat lastChat) {
        this.chatroomId = chatroom.getId();
        this.buyer = chatroom.getBuyer();
        this.seller = chatroom.getSeller();
        this.lastChat = new CustomChatResponse(lastChat);
        this.product = new CustomProductResponse(chatroom.getProduct());
    }

    public ChatroomResponseDto(Long chatroomId, TestUser buyer, TestUser seller, Chat lastChat) {
        this.chatroomId = chatroomId;
        this.buyer = buyer;
        this.seller = seller;
        this.lastChat = new CustomChatResponse(lastChat);
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
class CustomProductResponse {
    private final Long productId;
    private final String productName;

    public CustomProductResponse(TestProduct product) {
        this.productId = product.getId();
        this.productName = product.getName();
    }
}
