package com.team01.billage.chatting.domain;

import com.team01.billage.product.domain.Product;
import com.team01.billage.user.domain.Users;
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
    private Users buyer;

    // 판매자 ID
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Users seller;

    // 물품
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime buyerExitAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime sellerExitAt;

    public void setBuyerExitAt() {
        buyerExitAt = LocalDateTime.now();
    }

    public void setBuyerJoinAt() {
        buyerExitAt = null;
    }

    public void setSellerExitAt() {
        sellerExitAt = LocalDateTime.now();
    }

    public void setSellerJoinAt() {
        sellerExitAt = null;
    }

    public void addTestChat(Chat chat) {
        chats.add(chat);
    }

    public ChatRoom(Users buyer, Users seller, Product product) {
        this.buyer = buyer;
        this.seller = seller;
        this.product = product;
    }
}
