package com.team01.billage.chatting.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.QChat;
import com.team01.billage.chatting.domain.QChatRoom;
import com.team01.billage.user.domain.QUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatQueryDSL {
    private final JPAQueryFactory queryFactory;

    public List<Chat> getPagenatedChatsInChatroom(Long chatroomId, Long chatId, Long userId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        QChatRoom chatroom = QChatRoom.chatRoom;
        QChat chat = QChat.chat;
        QUsers seller = QUsers.users;
        QUsers buyer = new QUsers("buyer");

        builder.and(chat.chatRoom.id.eq(chatroomId));
        builder.and(chat.id.lt(chatId));

        BooleanExpression sellerCondition = chatroom.seller.id.eq(userId).and(chatroom.sellerExitAt.isNull());
        BooleanExpression buyerCondition = chatroom.buyer.id.eq(userId).and(chatroom.buyerExitAt.isNull());

        builder.and(sellerCondition.or(buyerCondition));

        List<Chat> chats = queryFactory.selectFrom(chat)
                .join(chat.chatRoom, chatroom)
                .leftJoin(chatroom.seller, seller)
                .leftJoin(chatroom.buyer, buyer)
                .where(builder)
                .orderBy(chat.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return chats;
    }
}
