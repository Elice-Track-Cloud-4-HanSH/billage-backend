package com.team01.billage.user.service;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Request.ProfileUpdateRequest;
import com.team01.billage.user.dto.Response.ProfileResponse;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.utils.s3.S3BucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final S3BucketService s3BucketService;

    public ProfileResponse getProfile(CustomUserDetails customUserDetails) {
        Users user = userRepository.findByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ProfileResponse.builder()
                .nickname(user.getNickname())
                .description(user.getDescription())
                .imageUrl(user.getImageUrl())
                .build();
    }

    @Transactional
    public ProfileResponse updateProfile(String email, ProfileUpdateRequest request, MultipartFile imageFile) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미지 처리 로직
        String imageUrl = user.getImageUrl(); // 기존 이미지 URL 보관

        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (imageUrl != null && !imageUrl.isEmpty()) {
                s3BucketService.delete(imageUrl);
            }
            // 새 이미지 업로드
            imageUrl = s3BucketService.upload(imageFile);
        }

        // 프로필 업데이트
        user.updateProfile(
                request.getNickname(),
                request.getDescription(),
                imageUrl
        );

        return ProfileResponse.builder()
                .nickname(user.getNickname())
                .description(user.getDescription())
                .imageUrl(user.getImageUrl())
                .build();
    }
}