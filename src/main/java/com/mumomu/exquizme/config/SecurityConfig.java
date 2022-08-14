package com.mumomu.exquizme.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.UUID;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // other public endpoints of your API may be appended to this array
            // basic whitelist
            "/",
            "/api/**",
            "/h2-console/**",
//            "/ws/**",
            "/stomp/**",
            "/sub/**",
            "/pub/**",
            "/topic/**",
            "/queue/**",
            "/quiz/**",
            "/index.html"
    };

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 로그인 필요 페이지 등록
        // permitAll 왼쪽에 넣을 시 불필요 도메인 ex) /problem/**
        // hasRole -> 해당 역할 필요
        // 우리 프로젝트에는 ADMIN, HOST, ANONYMOUS만 있을듯 함
        http.authorizeRequests()
                        .mvcMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated();

        //// H2 테스트 시 아래 두 줄 주석 해제해야 Spring Security가 H2를 차단 안함
        http.csrf().disable();
        http.headers().frameOptions().disable();

        http.cors();

        //http.anonymous().authorities("ROLE_USER");

        // 로그인 페이지 지정, 미 지정 시 기본 페이지 등장
//        http.formLogin()
//                .loginPage("/login")
//                .permitAll();

        // 로그아웃 페이지 지정, 미 지정 시 기본 페이지 등장
//        http.logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies();
    }
}
