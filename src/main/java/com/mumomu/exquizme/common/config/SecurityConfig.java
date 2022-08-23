package com.mumomu.exquizme.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.UUID;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
            "/stomp/**",
            "/sub/**",
            "/pub/**",
            "/topic/**",
            "/queue/**",
            "/quiz/**",
            "/index.html",
            "/google/**"
    };

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 생성자 통해 스프링 빈 주입받는다.
    public SecurityConfig(
            TokenProvider tokenProvider,
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    // BCryptPasswordEncoder 라는 패스워드 인코더 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2/**"
                        ,"/favicon.ico"
                        ,"/error"
                );
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 로그인 필요 페이지 등록
        // permitAll 왼쪽에 넣을 시 불필요 도메인 ex) /problem/**
        // hasRole -> 해당 역할 필요
        http.authorizeRequests()
                        .mvcMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated();

        // H2 테스트 시 아래 두 줄 주석 해제해야 Spring Security가 H2를 차단 안함
        http.csrf().disable()
                .headers().frameOptions().disable();

        http.cors();

        // google oauth2
        http
                .csrf().disable() // 토큰을 사용하는 방식이므로 csrf를 disable
                .addFilter(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) // 세션을 사용하기 때문에 always로 설정
                .and()
                .authorizeRequests()
                .mvcMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated() // 나머지 경로는 jwt 인증 필요
                .and()
                .apply(new JwtSecurityConfig(tokenProvider)); // JwtSecurityConfig 적용

        // 익명 사용자는 role_user 부여
        http.anonymous().authorities("ROLE_USER");

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
