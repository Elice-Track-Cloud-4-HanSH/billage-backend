package com.team01.billage.user.repository;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.team01.billage.user.domain.Provider.GOOGLE;
import static com.team01.billage.user.domain.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindByEmail() {
        // Given
        Users user = Users.builder()
                .nickname("testUser")
                .email("test@example.com")
                .password("password")
                .imageUrl("https://default-image.url")
                .role(USER)
                .provider(GOOGLE)
                .build();

        // When
        userRepository.save(user);

        // Then
        Optional<Users> foundUser = userRepository.findByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getNickname()).isEqualTo("testUser");
    }
}
