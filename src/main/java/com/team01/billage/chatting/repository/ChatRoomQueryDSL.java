package com.team01.billage.chatting.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.dao.ChatRoomWithLastChat;
import com.team01.billage.chatting.domain.QChat;
import com.team01.billage.chatting.domain.QChatRoom;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ChatRoomQueryDSL {
    private final JPAQueryFactory queryFactory;

    public List<ChatRoomWithLastChat> getChatrooms(String type, Long userId, Long productId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        QChatRoom chatroom = QChatRoom.chatRoom;
        QChat chat = QChat.chat;
        QChat chat2 = new QChat("chat2");

        BooleanExpression userCondition = switch (type) {
            case "LENT" -> getSellerCondition(chatroom, userId);
            case "RENT" -> getBuyerCondition(chatroom, userId);
            case "PR" -> {
                    BooleanExpression sellerCondition = getSellerCondition(chatroom, userId);
                    yield chatroom.product.id.eq(productId).and(sellerCondition);
            }
            // TYPE = ALL
            default -> {
                BooleanExpression buyerCondition = getBuyerCondition(chatroom, userId);
                BooleanExpression sellerCondition = getSellerCondition(chatroom, userId);
                yield sellerCondition.or(buyerCondition);
            }
        };
        builder.and(userCondition);

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
                .orderBy(chat.createdAt.desc(), chatroom.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression getSellerCondition(QChatRoom chatroom, Long userId) {
        return chatroom.seller.id.eq(userId).and(chatroom.sellerExitAt.isNull());
    }

    private BooleanExpression getBuyerCondition(QChatRoom chatroom, Long userId) {
        return chatroom.buyer.id.eq(userId).and(chatroom.buyerExitAt.isNull());
    }
}
