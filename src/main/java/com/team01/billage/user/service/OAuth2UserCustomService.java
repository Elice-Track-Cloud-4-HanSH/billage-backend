package com.team01.billage.user.service;

import com.team01.billage.config.oauth.OAuth2Attribute;
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
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2Attribute attributes = OAuth2Attribute.of(registrationId, userNameAttributeName,
                oAuth2User.getAttributes());

        Users user = upsert(attributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                attributes.convertToMap(),
                attributes.getNameAttributeKey());
    }

    private Users upsert(OAuth2Attribute attributes) {
        return userRepository.findByEmail(attributes.getEmail())
                .map(user -> {
                    // 이미 존재하는 회원이면 닉네임과 이미지 URL만 업데이트
                    user.setNickname(attributes.getName());
                    user.setImageUrl(attributes.getPicture());
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    // 새로운 회원이면 엔티티 생성 후 저장
                    Users newUser = Users.builder()
                            .email(attributes.getEmail())
                            .nickname(attributes.getName())
                            .imageUrl(attributes.getPicture())
                            .role(UserRole.USER)
                            .provider(Provider.GOOGLE)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
