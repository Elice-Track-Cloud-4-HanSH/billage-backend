package com.team01.billage.chatting.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.dao.ChatRoomWithLastChat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.QChat;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.chatting.domain.QTestUser;
import com.team01.billage.chatting.enums.ChatType;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ChatRoomQueryDSL {
    private final JPAQueryFactory queryFactory;

    public List<ChatRoomWithLastChat> getChatrooms(ChatType type, Long userId, Long productId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        QChatRoom chatroom = QChatRoom.chatRoom;
        QChat chat = QChat.chat;
        QChat chat2 = new QChat("chat2");

        switch (type) {
            case LENT -> builder.and(chatroom.seller.id.eq(userId));
            case RENT -> builder.and(chatroom.buyer.id.eq(userId));
            case PR -> {
                    if (productId == null) throw new RuntimeException("해당 조회건은 상품 id가 필수입니다");
                    builder.and(chatroom.product.id.eq(productId)).and(chatroom.seller.id.eq(userId));
            }
            // TYPE = ALL
            default -> builder.and(chatroom.seller.id.eq(userId).or(chatroom.buyer.id.eq(userId)));
        }

        return queryFactory
                .select(Projections.constructor(ChatRoomWithLastChat.class, chatroom, chat))
                .from(chatroom)
                .leftJoin(chatroom.chats, chat)
                .where(builder)
                .where(chat.id.eq(
                        JPAExpressions
                                .select(chat2.id.max())
                                .from(chat2)
                                .where(chat2.chatRoom.eq(chatroom))
                ))
                .orderBy(chatroom.id.desc(), chat.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}
