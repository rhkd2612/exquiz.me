package com.mumomu.exquizme.common.oauth;

import lombok.*;

import java.util.Map;


@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String nickname;
    private String email;
    private Role role;

    // 구글, 네이버 등 구분
    public static OAuthAttributes of(String registrationId, String usernameAttributeName, Map<String, Object> attributes){
        return ofGoogle(usernameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String usernameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .username((String)attributes.get("email"))
                .email((String)attributes.get("email"))
                .nickname((String)attributes.get("name"))
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    public User toEntity(){
        return User.builder()
                .username(email)
                .email(email)
                .nickname(nickname)
                .role(Role.SOCIAL)
                .build();
    }
}
