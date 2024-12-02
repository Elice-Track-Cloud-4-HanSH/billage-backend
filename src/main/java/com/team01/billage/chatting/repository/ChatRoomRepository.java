package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.ChatRoom;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE ChatRoom cr SET cr.sellerExitAt = CURRENT_TIMESTAMP WHERE cr.seller.id = :id")
    void exitSellerFromChatRoom(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE ChatRoom cr SET cr.buyerExitAt = CURRENT_TIMESTAMP WHERE cr.buyer.id = :id")
    void exitBuyerFromChatRoom(@Param("id") Long id);

    // QueryDSL이면 하나로 줄일 수 있지 않을까?
    @Query(value = "SELECT cr FROM ChatRoom cr JOIN cr.chats c WHERE cr.buyer.id = :userId OR cr.seller.id = :userId ORDER BY c.createdAt")
    List<ChatRoom> getAllChatroom(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT cr FROM ChatRoom cr JOIN cr.chats c WHERE cr.seller.id = :userId ORDER BY c.createdAt")
    List<ChatRoom> getToSellChatroom(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT cr FROM ChatRoom cr JOIN cr.chats c WHERE cr.buyer.id = :userId ORDER BY c.createdAt")
    List<ChatRoom> getToBuyChatroom(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.seller s " +
            "JOIN FETCH cr.buyer b " +
            "JOIN FETCH cr.product p " +
            "WHERE s.id = :sellerId AND b.id = :buyerId AND p.id = :productId")
    Optional<ChatRoom> checkChatroomIsExist(@Param("sellerId") Long sellerId, @Param("buyerId") Long buyerId, @Param("productId") Long productId);
}
