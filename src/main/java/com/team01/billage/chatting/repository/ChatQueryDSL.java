package com.team01.billage.chatting.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.QChat;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.chatting.dto.querydsl.ChatWithSenderDTO;
import com.team01.billage.chatting.dto.querydsl.ChatroomWithRecentChatDTO;
import com.team01.billage.user.domain.QUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatQueryDSL {
    private final JPAQueryFactory queryFactory;

    public List<ChatWithSenderDTO> getPagenatedChatsInChatroom(Long chatroomId, Long chatId, Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        QChatRoom qChatroom = QChatRoom.chatRoom;
        QChat qChat = QChat.chat;
        QUsers qSeller = QUsers.users;
        QUsers qBuyer = new QUsers("buyer");

        BooleanExpression sellerCondition = qChatroom.seller.id.eq(userId).and(qChatroom.sellerExitAt.isNull());
        BooleanExpression buyerCondition = qChatroom.buyer.id.eq(userId).and(qChatroom.buyerExitAt.isNull());

        List<ChatWithSenderDTO> chats = queryFactory
                .select(Projections.constructor(
                        ChatWithSenderDTO.class,
                        qChat.id,
                        Projections.constructor(ChatWithSenderDTO.User.class, qChat.sender.id, qChat.sender.nickname),
                        Projections.constructor(ChatWithSenderDTO.Message.class, qChat.message, qChat.createdAt),
                        qChat.isRead
                ))
                .from(qChat)
                .join(qChat.chatRoom, qChatroom)
                .leftJoin(qChatroom.seller, qSeller)
                .leftJoin(qChatroom.buyer, qBuyer)
                .where(
                        qChat.chatRoom.id.eq(chatroomId),
                        builder.and(qChat.id.lt(chatId)),
                        sellerCondition.or(buyerCondition)
                )
                .orderBy(qChat.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return chats;
    }
}
