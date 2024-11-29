package com.team01.billage.user.service;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = (String) oAuth2User.getAttributes().get("email");
        String name = (String) oAuth2User.getAttributes().get("name");
        String pictureUrl = (String) oAuth2User.getAttributes().get("picture");

        Users user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    // 기존 사용자의 경우 정보 업데이트
                    existingUser.setNickname(name);
                    existingUser.setImageUrl(pictureUrl);
                    return existingUser;
                })
                .orElse(Users.builder()
                        .email(email)
                        .nickname(name)
                        .imageUrl(pictureUrl)
                        .provider(Provider.GOOGLE)
                        .role(UserRole.USER)
                        .description("Google 로그인으로 가입한 회원입니다.")
                        .build());

        userRepository.save(user);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                oAuth2User.getAttributes(),
                "email");
    }
}