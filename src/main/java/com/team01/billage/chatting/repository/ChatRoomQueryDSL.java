package com.team01.billage.chatting.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.domain.QChat;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.chatting.dto.querydsl.ChatroomWithRecentChatDTO;
import com.team01.billage.product.domain.QProduct;
import com.team01.billage.user.domain.QUsers;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ChatRoomQueryDSL {
    private final JPAQueryFactory queryFactory;

    public List<ChatroomWithRecentChatDTO> getChatrooms(String type, Long userId, Long productId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        QChatRoom qChatroom = QChatRoom.chatRoom;
        QChat qChat = QChat.chat;
        QChat qChat2 = new QChat("chat2");
        QUsers qBuyer = new QUsers("buyer");
        QUsers qSeller = new QUsers("seller");
        QProduct qProduct = new QProduct("product");

        BooleanExpression userCondition = switch (type) {
            case "LENT" -> getSellerCondition(qChatroom, userId);
            case "RENT" -> getBuyerCondition(qChatroom, userId);
            case "PR" -> {
                    BooleanExpression sellerCondition = getSellerCondition(qChatroom, userId);
                    yield qChatroom.product.id.eq(productId).and(sellerCondition);
            }
            // TYPE = ALL
            default -> {
                BooleanExpression buyerCondition = getBuyerCondition(qChatroom, userId);
                BooleanExpression sellerCondition = getSellerCondition(qChatroom, userId);
                yield sellerCondition.or(buyerCondition);
            }
        };
        builder.and(userCondition);

        return queryFactory
                .select(Projections.constructor(
                        ChatroomWithRecentChatDTO.class,
                        qChatroom.id,
                        Projections.constructor(ChatroomWithRecentChatDTO.User.class, qBuyer.id, qBuyer.nickname),
                        Projections.constructor(ChatroomWithRecentChatDTO.User.class, qSeller.id, qSeller.nickname),
                        Projections.constructor(ChatroomWithRecentChatDTO.Chat.class, qChat.message, qChat.createdAt),
                        Projections.constructor(ChatroomWithRecentChatDTO.Product.class, qProduct.id, qProduct.title)
                ))
                .from(qChatroom)
                .leftJoin(qChatroom.buyer, qBuyer)
                .leftJoin(qChatroom.seller, qSeller)
                .leftJoin(qChatroom.product, qProduct)
                .leftJoin(qChatroom.chats, qChat)
                .where(
                        builder,
                        qChat.id.eq(
                                JPAExpressions
                                        .select(qChat2.id.max())
                                        .from(qChat2)
                                        .where(qChat2.chatRoom.eq(qChatroom))
                        )
                )
                .orderBy(qChat.createdAt.desc(), qChatroom.id.desc())
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
