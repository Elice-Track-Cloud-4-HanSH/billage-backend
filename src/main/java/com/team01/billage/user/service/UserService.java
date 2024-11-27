package com.team01.billage.user.service;

import static com.team01.billage.exception.ErrorCode.USER_NOT_FOUND;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.product_review.dto.ShowReviewResponseDto;
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
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto dto) {
        validateSignupRequest(dto);

        Users user = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                // 비밀번호 암호화
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getUserRole())
                .provider(dto.getProvider())
                .build();

        Users savedUser = userRepository.save(user);
        return savedUser.toResponseDto();
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

    public TargetProfileResponseDto showProfile(String nickname) {

        Users target = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        List<ShowReviewResponseDto> reviews = userReviewRepository.findByTarget_nickname(nickname);

        Double avgScore = userReviewRepository.scoreAverage(nickname)
            .map(score -> Math.round(score * 10) / 10.0).orElse(0.0);

        return TargetProfileResponseDto.builder()
            .imageUrl(target.getImageUrl())
            .nickname(target.getNickname())
            .description(target.getDescription())
            .avgScore(avgScore)
            .reviews(reviews)
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

        return new UserPasswordResponseDto(
                matches,
                matches ? "비밀번호가 일치합니다" : "비밀번호가 일치하지 않습니다"
        );
    }

    public boolean validateLoginRequest(JwtTokenLoginRequest request) {
        //아이디 값이 빈값이면 false
        String userRealId = request.getUserRealId();
        if (userRealId.isEmpty()) {
            return false;
        }

        //패스워드 값이 빈값이면 false
        String password = request.getPassword();
        if (password.isEmpty()) {
            return false;
        }

        return true;
    }
}