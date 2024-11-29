package com.team01.billage.chatting.domain;

import com.team01.billage.chatting.dto.ChatResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 ID
    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 발신 회원
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private TestUser sender;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP(0)", updatable = false)
    private LocalDateTime createdAt;

    public Chat(ChatRoom chatRoom, TestUser sender, String message) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.message = message;
    }

    @PrePersist
    public void initializeFields() {
        if (message.isEmpty()) {
            message = null;
        }
        isRead = false;
    }

    public ChatResponseDto toChatResponse() {
        return ChatResponseDto.builder()
                .chatId(this.id)
                .sender(this.sender)
                .message(this.message)
                .createdAt(this.createdAt)
                .build();
    }
}
