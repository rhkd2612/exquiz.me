package com.mumomu.exquizme.common.service;

import com.mumomu.exquizme.common.dto.GoogleLoginDto;
import com.mumomu.exquizme.common.dto.OAuth2AccountDto;
import com.mumomu.exquizme.common.entity.Role;
import com.mumomu.exquizme.common.entity.OAuth2Account;
import com.mumomu.exquizme.common.jwt.TokenProvider;
import com.mumomu.exquizme.common.repository.OAuth2AccountRepository;
import com.mumomu.exquizme.common.util.ConfigUtils;
import com.mumomu.exquizme.common.util.SecurityUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ObjectInputFilter;
import java.util.HashMap;
import java.util.Map;

@Service
public class OAuth2AccountService {
    private final OAuth2AccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ConfigUtils configUtils;

    private final static String NO_PASSWORD = "NO_PASSWORD";
    private final String GOOGLE_PROVIDER = "^google";

    public OAuth2AccountService(OAuth2AccountRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, ConfigUtils configUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.configUtils = configUtils;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @Transactional
    public OAuth2AccountDto signupWithGoogleOAuth2(GoogleLoginDto googleLoginDto) {
        final String username = googleLoginDto.getEmail() + GOOGLE_PROVIDER; // email + provider로 username 설정

        OAuth2Account user = userRepository.findByUsername(username).orElse(null);
        OAuth2AccountDto retOAuth2AccountDto = null;

        if (user == null) {
            // 이렇게 가입한 사람은 일반 출제자 ROLE_USER
            user = OAuth2Account.builder()
                    .username(username)
                    .nickname(googleLoginDto.getName())
                    .email(googleLoginDto.getEmail())
                    .password(passwordEncoder.encode(NO_PASSWORD))
                    .picture(googleLoginDto.getPicture())
                    .role(Role.USER)
                    .activated(true)
                    .build();

            retOAuth2AccountDto = OAuth2AccountDto.from(userRepository.save(user));
        }
        // throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");

        // 받아온 유저네임과 패스워드를 이용해 UsernamePasswordAuthenticationToken 객체 생성, oauth이므로 패스워드는 NO_PASSWORD
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, NO_PASSWORD);

        // authenticationToken 객체를 통해 Authentication 객체 생성
        // 이 과정에서 CustomUserDetailsService 에서 우리가 재정의한 loadUserByUsername 메서드 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 그 객체를 시큐리티 컨텍스트에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.createToken(authentication);

        retOAuth2AccountDto.setAccessToken(accessToken);

        return retOAuth2AccountDto;
    }

    @Transactional(readOnly = true)
    public OAuth2AccountDto getUserByUsername(String username) {
        return OAuth2AccountDto.from(userRepository.findByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public OAuth2AccountDto getGoogleUserByUsername(String username) {
        return OAuth2AccountDto.from(userRepository.findByUsername(username).orElse(null));
    }

    @Transactional(readOnly = true)
    public OAuth2AccountDto getMyUserByUsername() {
        return OAuth2AccountDto.from(
                SecurityUtil.getCurrentUsername()
                        .flatMap(userRepository::findByUsername)
                        .orElseThrow(() -> new NullPointerException("Member not found"))
        );
    }
}