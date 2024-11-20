package com.team01.billage.user.service;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.UserSignupRequestDto;
import com.team01.billage.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository; // UserRepository를 Mocking

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder; // BCryptPasswordEncoder를 Mocking

    @InjectMocks
    private UserService userService; // UserService를 Mock 객체들과 함께 주입

    private UserSignupRequestDto dto;

    @BeforeEach
    public void setup() {
        // 테스트 전에 DTO 객체 설정
        dto = new UserSignupRequestDto("nickname", "email@example.com", "password123");
    }

    @Test
    public void testSave_UserDoesNotExist() {
        // given
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);  // 이메일이 이미 존재하지 않음
        when(bCryptPasswordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");  // 비밀번호 암호화

        // when
        Users savedUser = userService.save(dto);

        // then
        assertNotNull(savedUser);  // 저장된 사용자 객체가 null이 아님을 확인
        assertEquals(dto.getEmail(), savedUser.getEmail());  // 이메일이 같음을 확인
        assertEquals(dto.getNickname(), savedUser.getNickname());  // 닉네임이 같음을 확인
        assertEquals("encodedPassword", savedUser.getPassword());  // 암호화된 비밀번호가 맞는지 확인
        assertEquals(UserRole.USER.toString(), savedUser.getRole());  // 역할이 USER인지를 확인
        assertEquals(Provider.NONE.toString(), savedUser.getProvider());  // provider가 NONE인지 확인
    }

    @Test
    public void testSave_UserAlreadyExists() {
        // given
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);  // 이메일이 이미 존재한다고 설정

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.save(dto);
        });
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());  // 예외 메시지가 정확한지 확인
    }
}
