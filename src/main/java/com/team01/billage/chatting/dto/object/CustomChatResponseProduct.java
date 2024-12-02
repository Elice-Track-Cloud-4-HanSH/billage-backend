package com.team01.billage.chatting.dto.object;

import com.team01.billage.product.domain.Product;
import lombok.Getter;

@Getter
public class CustomChatResponseProduct {
    private final Long id;
    private final String name;

    public CustomChatResponseProduct(Product product) {
        this.id = product.getId();
        this.name = product.getTitle();
    }
}
