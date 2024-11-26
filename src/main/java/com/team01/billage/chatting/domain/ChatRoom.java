package com.team01.billage.chatting.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"buyer_id", "seller_id", "product_id"})
)
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Chat> chats = new ArrayList<>();

    // 구매자 ID
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private TestUser buyer;

    // 판매자 ID
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private TestUser seller;

    // 물품
    @ManyToOne
    @JoinColumn(name = "product_id")
    private TestProduct product;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime buyerExitAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime sellerExitAt;

    public void setBuyerExitAt() {
        buyerExitAt = LocalDateTime.now();
    }

    public void setSellerExitAt() {
        sellerExitAt = LocalDateTime.now();
    }

    public void addTestChat(Chat chat) {
        chats.add(chat);
    }

    public ChatRoom(TestUser buyer, TestUser seller, TestProduct product) {
        this.buyer = buyer;
        this.seller = seller;
        this.product = product;
    }
}
