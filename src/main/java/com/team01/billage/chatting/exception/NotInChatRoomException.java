package com.team01.billage.chatting.exception;

import java.io.Serial;

public class NotInChatRoomException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public NotInChatRoomException(String message) {
        super(message);
    }
}
