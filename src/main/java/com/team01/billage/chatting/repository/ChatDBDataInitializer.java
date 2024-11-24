package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestProduct;
import com.team01.billage.chatting.domain.TestUser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatDBDataInitializer implements CommandLineRunner {
    private final TestUserRepository userRepository;
    private final TestProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public void run(String... args) throws Exception {
        TestUser buyer = TestUser.builder()
                .nickname("유저1")
                .build();
        TestUser seller = TestUser.builder()
                .nickname("유저2")
                .build();

        userRepository.saveAll(List.of(buyer, seller));

        TestProduct product = TestProduct.builder()
                .name("상품1")
                .build();

        productRepository.save(product);

        ChatRoom chatroom = ChatRoom.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .build();

        chatRoomRepository.save(chatroom);
    }
}
