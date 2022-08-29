package com.mumomu.exquizme.common.controller;

import com.mumomu.exquizme.common.dto.OAuth2AccountDto;
import com.mumomu.exquizme.common.service.OAuth2AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class OAuth2AccountController {
    private final OAuth2AccountService oAuth2AccountService;
    private Map<String, Object> loginMap = new HashMap<>();

    public OAuth2AccountController(OAuth2AccountService oAuth2AccountService) {
        this.oAuth2AccountService = oAuth2AccountService;
    }

    // 이후 로그인별 분기
//    @PostConstruct
//    void initLoginMap(){
//        loginMap.put("google", userService.getGoogleUserByUsername(T))
//    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<OAuth2AccountDto> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(oAuth2AccountService.getMyUserByUsername());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        if(username.contains(oAuth2AccountService.getCONNECT_CHAR())) {
            String[] splitUsername = username.split(oAuth2AccountService.getCONNECT_CHAR());

            log.info(splitUsername[1] + " connection access.");

            if (splitUsername[1].equals(oAuth2AccountService.getGOOGLE_PROVIDER()))
                return ResponseEntity.ok(oAuth2AccountService.getGoogleUserByUsername(username));
        }

        return new ResponseEntity<>("잘못된 uri 요청입니다", HttpStatus.BAD_REQUEST);
    }
}