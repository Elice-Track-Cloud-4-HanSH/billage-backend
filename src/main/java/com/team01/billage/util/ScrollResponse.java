package com.team01.billage.util;

import org.joda.time.LocalDateTime;

public class ScrollResponse<T> {

    private T data;
    private LocalDateTime nextCursor;
    private boolean hasNext;
}
