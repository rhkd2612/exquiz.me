package com.mumomu.exquizme.common.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Getter
public class ConfigUtils {
    @Value("${spring.security.oauth2.client.registration.google.auth-url}")
    private String googleAuthUrl;
    @Value("${spring.security.oauth2.client.registration.google.login-url}")
    private String googleLoginUrl;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleSecret;
    private String frontendUrl = "http://localhost:3000";//"https://www.exquiz.me";
    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String scopes;

    // Google 로그인 URL 생성 로직
    public String googleInitUrl(){
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", getGoogleClientId());
        params.put("redirect_uri", getGoogleRedirectUri());
        params.put("response_type", "code");
        params.put("scope", getScopeUrl());

        String paramStr = params.entrySet().stream()
                .map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return getGoogleLoginUrl()
                + "/o/oauth2/v2/auth"
                + "?"
                + paramStr;
    }

    private String getScopeUrl() {
        return scopes.replaceAll(",","%20");
    }
}
