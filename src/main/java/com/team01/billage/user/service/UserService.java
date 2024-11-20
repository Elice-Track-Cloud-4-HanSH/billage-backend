package com.team01.billage.user.service;


import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.UserResponseDto;
import com.team01.billage.user.dto.UserSignupRequestDto;
import com.team01.billage.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.team01.billage.user.domain.Provider;

import java.util.List;
import java.util.stream.Collectors;

// 필요한 의존성 주입 (Repository, PasswordEncoder)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Users save(UserSignupRequestDto dto) {   //저장
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        Users user = Users.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .role(UserRole.USER) // Enum 값 사용
                .provider(Provider.NONE) // Enum 값 사용
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
}

