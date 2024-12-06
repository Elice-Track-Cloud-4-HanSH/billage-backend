package com.team01.billage.user.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Request.UserUpdateRequestDto;
import com.team01.billage.user.dto.Response.TargetProfileResponseDto;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserPasswordResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.user_review.repository.UserReviewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserReviewRepository userReviewRepository;
    private final JavaMailSender emailSender;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto dto) {
        validateSignupRequest(dto);

        // 이메일 인증 여부 확인
        String verified = redisTemplate.opsForValue().get("EmailVerified:" + dto.getEmail());
        if (verified == null) {
            throw new RuntimeException("이메일 인증이 필요합니다.");
        }

        Users user = Users.builder()
            .nickname(dto.getNickname())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .role(dto.getUserRole())
            .provider(dto.getProvider())
            .build();

        Users savedUser = userRepository.save(user);

        // Redis에서 인증 정보 삭제
        redisTemplate.delete("EmailVerified:" + dto.getEmail());

        return savedUser.toResponseDto();
    }


    // 이메일 인증 코드 발송
    public void sendVerificationEmail(String email) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String verificationCode = generateRandomCode();

        // Redis에 이메일 인증 코드 저장 (30분 유효)
        redisTemplate.opsForValue().set(
            "EmailAuth:" + email,
            verificationCode,
            Duration.ofMinutes(30)
        );

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kakaobank0314@naver.com");  // 네이버 계정 이메일 추가
        message.setTo(email);
        message.setSubject("[Billage] 이메일 인증");
        message.setText("인증 코드: " + verificationCode + "\n\n"
            + "이 코드는 30분 동안 유효합니다.");
        emailSender.send(message);
    }

    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    /**
     * 회원 정보 조회
     */
    public Users findByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 전체 회원 조회
     */
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
            .filter(user -> !user.isDeleted())
            .map(Users::toResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * 회원 정보 수정
     */
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto dto) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        updateUserFields(user, dto);
        Users updatedUser = userRepository.save(user);
        return updatedUser.toResponseDto();
    }

    /**
     * 회원 삭제 (소프트 삭제)
     */
    @Transactional
    public UserDeleteResponseDto deleteUser(String email) {
        Users user = findByEmail(email);
        if (user.isDeleted()) {
            throw new CustomException(ErrorCode.USER_ALREADY_DELETED);
        }
        return user.deleteUser();
    }

    /**
     * 이메일 중복 확인
     */
    @Operation(summary = "이메일 중복 확인 API", description = "사용자가 입력한 이메일이 중복인지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }

    /**
     * 닉네임 중복 확인
     */
    @Operation(summary = "닉네임 중복 확인 API", description = "사용자가 입력한 닉네임이 중복인지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
        @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임")
    })
    public void validateNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    public TargetProfileResponseDto showProfile(long userId) {
        Users target = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Double avgScore = userReviewRepository.scoreAverage(userId)
            .map(score -> Math.round(score * 10) / 10.0).orElse(0.0);

        Integer reviewCount = userReviewRepository.reviewCount(userId).orElse(0);

        return TargetProfileResponseDto.builder()
            .imageUrl(target.getImageUrl())
            .nickname(target.getNickname())
            .description(target.getDescription())
            .avgScore(avgScore)
            .reviewCount(reviewCount)
            .build();
    }

    // Private helper methods
    private void validateSignupRequest(UserSignupRequestDto dto) {
        validateEmail(dto.getEmail());
        validateNickname(dto.getNickname());
    }

    private void updateUserFields(Users user, UserUpdateRequestDto dto) {
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            validateNickname(dto.getNickname());
            user.setNickname(dto.getNickname());
        }
        // 비밀번호 변경 시 암호화
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            user.setImageUrl(dto.getImageUrl());
        }
        if (dto.getDescription() != null) {
            user.setDescription(dto.getDescription());
        }
    }

    /**
     * 비밀번호 확인
     */
    public UserPasswordResponseDto verifyPassword(String email, String rawPassword) {
        Users user = findByEmail(email);

        // 입력받은 평문 비밀번호와 저장된 암호화된 비밀번호를 비교
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (!matches) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        return new UserPasswordResponseDto(true, "비밀번호가 일치합니다");
    }

    public boolean validateLoginRequest(JwtTokenLoginRequest request) {
        //아이디 값이나 패스워드가 비어있으면 예외 발생
        if (request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_LOGIN_REQUEST);
        }
        return true;
    }

    // 이메일 인증 코드 검증
    public void verifyEmail(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get("EmailAuth:" + email);
        if (savedCode == null) {
            throw new CustomException(ErrorCode.EXPIRED_EMAIL_CODE);
        }

        if (!savedCode.equals(code)) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_CODE);
        }

        // 인증 성공 시 Redis에 인증 완료 상태 저장 (회원가입 완료 전까지 30분 유효)
        redisTemplate.opsForValue().set(
            "EmailVerified:" + email,
            "true",
            Duration.ofMinutes(30)
        );

        // 기존 인증 코드는 삭제
        redisTemplate.delete("EmailAuth:" + email);
    }

}