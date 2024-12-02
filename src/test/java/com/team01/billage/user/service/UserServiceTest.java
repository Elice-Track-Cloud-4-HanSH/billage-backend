package com.team01.billage.user.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Request.UserUpdateRequestDto;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import com.team01.billage.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserSignupRequestDto signupRequestDto;
    private Users mockUser;

    @BeforeEach
    void setUp() {
        signupRequestDto = UserSignupRequestDto.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("testUser")
                .userRole(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();

        mockUser = Users.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .nickname("testUser")
                .role(UserRole.USER)
                .provider(Provider.LOCAL)
                .build();
    }

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {

        @Test
        @DisplayName("정상적인 회원가입 성공")
        void signupSuccess() {
            // given
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.existsByNickname(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
            given(userRepository.save(any(Users.class))).willReturn(mockUser);

            // when
            UserResponseDto result = userService.signup(signupRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(signupRequestDto.getEmail());
            assertThat(result.getNickname()).isEqualTo(signupRequestDto.getNickname());
            verify(userRepository).save(any(Users.class));
        }

        @Test
        @DisplayName("중복된 이메일로 회원가입 실패")
        void signupFailWithDuplicateEmail() {
            // given
            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signup(signupRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("회원 조회 테스트")
    class FindUserTest {

        @Test
        @DisplayName("이메일로 회원 조회 성공")
        void findByEmailSuccess() {
            // given
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString()))
                    .willReturn(Optional.of(mockUser));

            // when
            Users result = userService.findByEmail("test@example.com");

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(mockUser.getEmail());
        }

        @Test
        @DisplayName("전체 회원 조회 성공")
        void findAllSuccess() {
            // given
            Users user1 = mockUser;
            Users user2 = Users.builder()
                    .id(2L)
                    .email("test2@example.com")
                    .nickname("testUser2")
                    .build();
            given(userRepository.findAll()).willReturn(Arrays.asList(user1, user2));

            // when
            List<UserResponseDto> results = userService.findAll();

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).getEmail()).isEqualTo(user1.getEmail());
            assertThat(results.get(1).getEmail()).isEqualTo(user2.getEmail());
        }
    }

    @Nested
    @DisplayName("회원 정보 수정 테스트")
    class UpdateUserTest {

        @Test
        @DisplayName("회원 정보 수정 성공")
        void updateUserSuccess() {
            // given
            UserUpdateRequestDto updateDto = UserUpdateRequestDto.builder()
                    .nickname("newNickname")
                    .password("newPassword")
                    .description("new description")
                    .build();

            given(userRepository.findById(anyLong())).willReturn(Optional.of(mockUser));
            given(userRepository.existsByNickname(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("newEncodedPassword");
            given(userRepository.save(any(Users.class))).willReturn(mockUser);

            // when
            UserResponseDto result = userService.updateUser(1L, updateDto);

            // then
            assertThat(result).isNotNull();
            verify(userRepository).save(any(Users.class));
        }
    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    class DeleteUserTest {

        @Test
        @DisplayName("회원 소프트 삭제 성공")
        void deleteUserSuccess() {
            // given
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString()))
                    .willReturn(Optional.of(mockUser));

            // when
            UserDeleteResponseDto result = userService.deleteUser("test@example.com");

            // then
            assertThat(result.isDeleted()).isTrue();
            assertThat(result.getMessage()).isEqualTo("회원 삭제 성공");
        }

        @Test
        @DisplayName("이미 삭제된 회원 삭제 시도")
        void deleteAlreadyDeletedUser() {
            // given
            mockUser.setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
            given(userRepository.findByEmailAndDeletedAtIsNull(anyString()))
                    .willThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> userService.deleteUser("test@example.com"))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("유효성 검사 테스트")
    class ValidationTest {

        @Test
        @DisplayName("이메일 중복 검사 성공")
        void validateEmailSuccess() {
            // given
            given(userRepository.existsByEmail(anyString())).willReturn(false);

            // when & then
            assertThat(catchThrowable(() -> userService.validateEmail("test@example.com")))
                    .isNull();
        }

        @Test
        @DisplayName("닉네임 중복 검사 성공")
        void validateNicknameSuccess() {
            // given
            given(userRepository.existsByNickname(anyString())).willReturn(false);

            // when & then
            assertThat(catchThrowable(() -> userService.validateNickname("testUser")))
                    .isNull();
        }
    }
}