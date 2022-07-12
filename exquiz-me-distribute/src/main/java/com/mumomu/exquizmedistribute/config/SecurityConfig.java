package com.mumomu.exquizmedistribute.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 로그인 필요 페이지 등록
        // permitAll 왼쪽에 넣을 시 불필요 도메인 ex) /problem/**
        // hasRole -> 해당 역할 필요
        // 우리 프로젝트에는 ADMIN, HOST, ANONYMOUS만 있을듯 함
        http.authorizeRequests()
                .mvcMatchers("/").permitAll()
                .mvcMatchers("/user/**").hasRole("HOST")
                .anyRequest().authenticated();

        // 로그인 페이지 지정, 미 지정 시 기본 페이지 등장
//        http.formLogin()
//                .loginPage("/login")
//                .permitAll();

        http.httpBasic();

        // 로그아웃 페이지 지정, 미 지정 시 기본 페이지 등장
//        http.logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .invalidateHttpSession(true)
//                .deleteCookies();
    }
}
