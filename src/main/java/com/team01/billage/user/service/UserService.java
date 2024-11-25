package com.team01.billage.user.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Response.*;
import com.team01.billage.user.dto.Request.*;
import com.team01.billage.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     */
    @Transactional
    public UserResponseDto signup(UserSignupRequestDto dto) {
        validateSignupRequest(dto);

        Users user = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
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
    public UserResponseDto updateUser(Long userId, @Valid UserUpdateRequestDto dto) {
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
}