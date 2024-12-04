package com.team01.billage.common;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@Getter
public class CustomSlice<T> extends SliceImpl<T> {

    private final LocalDateTime lastTime;

    public CustomSlice(List<T> content, Pageable pageable, boolean hasNext,
        LocalDateTime lastTime) {
        super(content, pageable, hasNext);
        this.lastTime = lastTime;
    }

}

