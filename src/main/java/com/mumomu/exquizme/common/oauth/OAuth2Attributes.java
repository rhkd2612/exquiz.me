package com.mumomu.exquizme.common.oauth;

import com.mumomu.exquizme.common.entity.Role;
import com.mumomu.exquizme.common.entity.OAuth2Account;
import lombok.*;

import java.util.Map;


@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuth2Attributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String nickname;
    private String email;
    private Role role;

    // 구글, 네이버 등 구분
    public static OAuth2Attributes of(String registrationId, String usernameAttributeName, Map<String, Object> attributes){
        return ofGoogle(usernameAttributeName, attributes);
    }

    private static OAuth2Attributes ofGoogle(String usernameAttributeName, Map<String, Object> attributes){
        return OAuth2Attributes.builder()
                .username((String)attributes.get("email"))
                .email((String)attributes.get("email"))
                .nickname((String)attributes.get("name"))
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    public OAuth2Account toEntity(){
        return OAuth2Account.builder()
                .username(email)
                .email(email)
                .nickname(nickname)
                .role(Role.SOCIAL)
                .build();
    }
}
