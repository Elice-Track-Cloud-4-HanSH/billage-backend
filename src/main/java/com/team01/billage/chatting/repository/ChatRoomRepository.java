package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query(value = "SELECT COUNT(cr) FROM ChatRoom cr WHERE cr.buyer.id = :userId OR cr.seller.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT cr FROM ChatRoom cr " +
            "JOIN FETCH cr.seller s " +
            "JOIN FETCH cr.buyer b " +
            "JOIN FETCH cr.product p " +
            "WHERE s.id = :sellerId AND b.id = :buyerId AND p.id = :productId")
    Optional<ChatRoom> checkChatroomIsExist(@Param("sellerId") Long sellerId, @Param("buyerId") Long buyerId, @Param("productId") Long productId);
}
