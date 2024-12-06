package com.team01.billage.common;

import java.util.List;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

@Getter
public class CustomSlice<T, T2> extends SliceImpl<T> {

    private final T2 lastStandard;

    public CustomSlice(List<T> content, Pageable pageable, boolean hasNext,
        T2 lastStandard) {
        super(content, pageable, hasNext);
        this.lastStandard = lastStandard;
    }

}

