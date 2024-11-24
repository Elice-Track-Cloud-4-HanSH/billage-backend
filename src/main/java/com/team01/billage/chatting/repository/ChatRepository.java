package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.Chat;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
//    @Query(value = "SELECT c FROM Chat c JOIN FETCH ChatRoom cr ON c.chatRoom = cr WHERE cr.id = :chatroomId ORDER BY c.createdAt DESC")
    @Query(value = "SELECT c FROM Chat c WHERE c.chatRoom.id = :chatroomId ORDER BY c.createdAt DESC")
    List<Chat> getAllChatsInChatroom(@Param("chatroomId") Long chatroomId, Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Chat c WHERE c.chatRoom.id = :chatroomId AND c.sender.id != :userId AND c.isRead = true ORDER BY c.createdAt DESC")
    List<Chat> getLastReadChat(@Param("chatroomId") Long chatroomId, @Param("userId") Long userId, Pageable pageable);

    @Query(value = "SELECT DISTINCT c FROM Chat c WHERE c.chatRoom.id = :chatroomId AND c.sender.id != :userId ORDER BY c.createdAt ASC")
    List<Chat> getFirstChat(@Param("chatroomId") Long chatroomId, @Param("userId") Long userId, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Chat c SET c.isRead = true WHERE c.chatRoom.id = :chatroomId AND c.createdAt >= :lastReadDate AND c.sender.id != :userId")
    void markAsRead(@Param("chatroomId") Long chatroomId, @Param("userId") Long userId, @Param("lastReadDate") LocalDateTime lastReadDate);
}
