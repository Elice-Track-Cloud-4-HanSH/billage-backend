package com.team01.billage.chatting.domain;

import com.team01.billage.product.domain.Product;
import com.team01.billage.user.domain.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
