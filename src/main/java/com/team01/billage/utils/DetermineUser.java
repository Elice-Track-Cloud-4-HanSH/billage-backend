package com.team01.billage.utils;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DetermineUser {
    private final UserRepository userRepository;

    public Users determineUser(String emailOrUserId) {
        try {
            Long userId = Long.parseLong(emailOrUserId);
            return userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        } catch (NumberFormatException e) {
            return userRepository.findByEmail(emailOrUserId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        }
    }
}
