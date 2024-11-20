package com.team01.billage.user.service;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.UserResponseDto;
import com.team01.billage.user.dto.UserSignupRequestDto;
import com.team01.billage.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

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
    private UserSignupRequestDto dto2;

    @BeforeEach
    public void setup() {
        // 테스트 전에 DTO 객체 설정
        dto = new UserSignupRequestDto("nickname", "email@example.com", "password123",UserRole.USER,Provider.NONE);
        dto2 = new UserSignupRequestDto("ppp", "google@example.com", "password456",UserRole.ADMIN,Provider.GOOGLE);
    }

    @Test
    public void testSave_UserDoesNotExist() {
        // given
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);  // 이메일이 이미 존재하지 않음
        when(bCryptPasswordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");  // 비밀번호 암호화

        // 가짜로 저장될 Users 객체 생성 및 반환 설정
        Users mockUser = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password("encodedPassword")
                .role(UserRole.USER)
                .provider(Provider.NONE)
                .build();

        when(userRepository.save(any(Users.class))).thenReturn(mockUser);  // save 호출 시 mockUser 반환

        // when
        Users savedUser = userService.save(dto);

        // then
        assertNotNull(savedUser);  // 저장된 사용자 객체가 null이 아님을 확인
        assertEquals(dto.getEmail(), savedUser.getEmail());  // 이메일이 같음을 확인
        assertEquals(dto.getNickname(), savedUser.getNickname());  // 닉네임이 같음을 확인
        assertEquals("encodedPassword", savedUser.getPassword());  // 암호화된 비밀번호가 맞는지 확인
        assertEquals(UserRole.USER, savedUser.getRole());  // 역할이 USER인지를 확인
        assertEquals(Provider.NONE, savedUser.getProvider());  // provider가 NONE인지 확인

        // Mock 메서드 호출 확인
        verify(userRepository).existsByEmail(dto.getEmail());  // existsByEmail 호출 확인
        verify(userRepository).save(any(Users.class));  // save 호출 확인
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

    @Test
    void findAll() {
        //given
        Users user1 = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password("encodedPassword1")
                .role(dto.getUserRole()) // Enum을 String으로 변환
                .provider(dto.getProvider()) // Enum을 String으로 변환
                .imageUrl("https://example.com/user1.png")
                .description("Test user 1 description")
                .build();

        Users user2 = Users.builder()
                .nickname(dto2.getNickname())
                .email(dto2.getEmail())
                .password("encodedPassword2")
                .role(dto2.getUserRole()) // Enum을 String으로 변환
                .provider(dto2.getProvider()) // Enum을 String으로 변환
                .imageUrl("https://example.com/user2.png")
                .description("Test user 2 description")
                .build();

        // Mock 데이터 설정
        List<Users> mockUsers = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(mockUsers); // userRepository의 findAll 호출 시 mockUsers 반환

        // when
        List<UserResponseDto> result = userService.findAll(); // 테스트 대상 메서드 호출

        // then
        assertEquals(2, result.size()); // 반환된 결과의 크기 확인

        // 첫 번째 UserResponseDto 확인
        UserResponseDto response1 = result.get(0);
        assertEquals(dto.getNickname(), response1.getNickname());
        assertEquals(dto.getEmail(), response1.getEmail());
        assertEquals(UserRole.USER, response1.getRole());
        assertEquals(Provider.NONE, response1.getProvider());
        assertEquals("https://example.com/user1.png", response1.getImageUrl());
        assertEquals("Test user 1 description", response1.getDescription());

        // 두 번째 UserResponseDto 확인
        UserResponseDto response2 = result.get(1);
        assertEquals(dto2.getNickname(), response2.getNickname());
        assertEquals(dto2.getEmail(), response2.getEmail());
        assertEquals(UserRole.ADMIN, response2.getRole());
        assertEquals(Provider.GOOGLE, response2.getProvider());
        assertEquals("https://example.com/user2.png", response2.getImageUrl());
        assertEquals("Test user 2 description", response2.getDescription());

        // Mock 메서드 호출 확인
        verify(userRepository).findAll(); // findAll이 호출되었는지 확인

    }


}
