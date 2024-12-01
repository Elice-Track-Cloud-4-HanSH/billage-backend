package com.team01.billage.chatting.repository;

import com.team01.billage.chatting.domain.Chat;
import com.team01.billage.chatting.domain.ChatRoom;
import com.team01.billage.chatting.domain.TestProduct;
import com.team01.billage.chatting.domain.TestUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

//@Component
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

        List<TestProduct> products = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            products.add(new TestProduct(String.format("상품 %d", i + 1)));
        }
        productRepository.saveAll(products);

        List<ChatRoom> chatrooms = new ArrayList<>();
        for (TestProduct product : products) {
            ChatRoom chatRoom = new ChatRoom(buyer, seller, product);
            Chat chat = new Chat(chatRoom, buyer, "Hello World + %d");
            chatRoom.addTestChat(chat);
            chatrooms.add(chatRoom);
        }
        chatRoomRepository.saveAll(chatrooms);
    }
}
