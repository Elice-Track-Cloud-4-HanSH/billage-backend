package com.team01.billage.chatting.dto;

import com.team01.billage.chatting.dto.object.CustomChatResponse;
import com.team01.billage.chatting.dto.object.CustomChatResponseProduct;
import com.team01.billage.chatting.dto.object.CustomChatResponseUser;
import com.team01.billage.chatting.dto.querydsl.ChatroomWithRecentChatDTO;
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

    public ChatroomResponseDto(ChatroomWithRecentChatDTO dto, Long userId, Long unreadCount) {
        this.chatroomId = dto.getChatroomId();
        this.buyer = new CustomChatResponseUser(dto.getBuyer().getId(), dto.getBuyer().getNickname(), dto.getBuyer().getProfileUrl());
        this.seller = new CustomChatResponseUser(dto.getSeller().getId(), dto.getSeller().getNickname(), dto.getSeller().getProfileUrl());
        this.lastChat = new CustomChatResponse(dto.getLastChat().getMessage(), dto.getLastChat().getLastSentTime());
        this.product = new CustomChatResponseProduct(dto.getProduct().getId(), dto.getProduct().getName(), dto.getProduct().getImageUrl());
        this.opponent = userId.equals(this.buyer.getId()) ? this.seller : this.buyer;
        this.unreadCount = unreadCount;
    }
}


