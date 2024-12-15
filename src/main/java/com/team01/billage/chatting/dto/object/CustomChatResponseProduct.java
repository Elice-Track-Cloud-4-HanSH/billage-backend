package com.team01.billage.chatting.dto.object;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomChatResponseProduct {
    private final Long id;
    private final String name;
    private final String thumbnail;
}
