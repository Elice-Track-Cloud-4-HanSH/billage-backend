package com.team01.billage.user.service;


import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Request.UserUpdateRequestDto;
import com.team01.billage.user.dto.UserDto;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// 필요한 의존성 주입 (Repository, PasswordEncoder)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //회원 저장
    public Users save(UserSignupRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Users user = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .role(dto.getUserRole()) // Enum 값 사용
                .provider(dto.getProvider()) // Enum 값 사용
                .build();

        return userRepository.save(user);
    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->new IllegalArgumentException("email 없음"));
    }

    //전체 확인
    public List<UserResponseDto> findAll() {
        List<Users> users = userRepository.findAll();

        return users.stream()
                .map(Users::toResponseDto)  // Users 객체를 UserResponseDto로 변환
                .collect(Collectors.toList());
    }


    //회원 업데이트
    public Users updateUser(Long userId, UserUpdateRequestDto dto) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));


        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        }
        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            user.setImageUrl(dto.getImageUrl());
        }
        if (dto.getDescription() != null) {
            user.setDescription(dto.getDescription());
        }

        return userRepository.save(user);
    }

    public boolean isDuplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public UserDeleteResponseDto softDeleteUser(UserDto userDto) {
        Users user = findByEmail(userDto.getEmail());
        return user.deleteUser();
    }
}

