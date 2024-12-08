package com.team01.billage.chatting.dao;

import com.team01.billage.user.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CheckValidChatroomDao {
    private Users buyer;
    private Long sellerId;
    private Long productId;
}
