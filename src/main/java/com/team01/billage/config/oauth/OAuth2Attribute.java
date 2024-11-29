package com.team01.billage.config.oauth;

import com.team01.billage.user.domain.Provider;
import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.domain.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public static OAuth2Attribute of(String provider, String attributeKey,
                                     Map<String, Object> attributes) {
        return ofGoogle(attributeKey, attributes);
    }

    private static OAuth2Attribute ofGoogle(String attributeKey,
                                            Map<String, Object> attributes) {
        return new OAuth2Attribute(
                attributes,
                attributeKey,
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("picture")
        );
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", nameAttributeKey);
        map.put("key", nameAttributeKey);
        map.put("name", name);
        map.put("email", email);
        map.put("picture", picture);
        map.put("sub", email);  // email을 sub 값으로 추가

        return map;
    }

    public Users toEntity() {
        return Users.builder()
                .nickname(name)
                .email(email)
                .imageUrl(picture)
                .role(UserRole.USER)
                .provider(Provider.GOOGLE)
                .build();
    }
}
