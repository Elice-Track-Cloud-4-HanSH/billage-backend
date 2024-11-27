package com.team01.billage.chatting.dao;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatRoomWithLastChat {
    private final ChatRoom chatRoom;
    private final Chat lastChat;
}
