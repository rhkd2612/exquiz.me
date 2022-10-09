package com.mumomu.exquizme.common.service;

import com.mumomu.exquizme.common.entity.OAuth2Account;
import com.mumomu.exquizme.common.repository.OAuth2AccountRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final OAuth2AccountRepository userRepository;

    public CustomUserDetailsService(OAuth2AccountRepository OAuth2AccountRepository) {
        this.userRepository = OAuth2AccountRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        OAuth2Account oAuth2Account = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));

        if (!oAuth2Account.isActivated())
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(oAuth2Account.getRoleValue());

        return new User(
                oAuth2Account.getUsername(),
                oAuth2Account.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }
}