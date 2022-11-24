package com.mumomu.exquizme.common.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.mumomu.exquizme.common.dto.GoogleLoginDto;
import com.mumomu.exquizme.common.dto.GoogleLoginRequest;
import com.mumomu.exquizme.common.dto.GoogleLoginResponse;
import com.mumomu.exquizme.common.dto.OAuth2AccountDto;
import com.mumomu.exquizme.common.service.OAuth2AccountService;
import com.mumomu.exquizme.common.util.ConfigUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/google")
@Api(tags = {"구글 소셜 로그인에 필요한 컨트롤러"})
public class GoogleOAuth2Controller {
    private final ConfigUtils configUtils;
    private final OAuth2AccountService oAuth2AccountService;

    @GetMapping(value = "/login/{type}")
    @Operation(summary = "구글 로그인 페이지", description = "구글에 사용자 아이디로 로그인을 요청할 수 있는 페이지")
    @ApiResponse(responseCode = "303", description = "로그인 성공 -> 홈씬으로 이동")
    @ApiResponse(responseCode = "400", description = "잘못된 로그인 요청")
    public ResponseEntity<Object> moveGoogleInitUrl(@PathVariable String type) {

        String authUrl = "";

        if(type.equals("local"))
            authUrl = configUtils.googleInitLocalUrl();
        else if(type.equals("prod"))
            authUrl = configUtils.googleInitProdUrl();
        else
            return ResponseEntity.badRequest().build();

        URI redirectUri = null;
        try {
            redirectUri = new URI(authUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(redirectUri);

            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (URISyntaxException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/login/redirect/local")
    @Operation(summary = "구글 로그인 리다이렉션 페이지(로컬용)", description = "구글 로그인 시 코드 반환")
    public void redirectGoogleLoginLocal(
            @RequestParam(value = "code") String authCode, HttpServletResponse response) throws IOException {
        // HTTP 통신을 위해 RestTemplate 활
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(configUtils.getGoogleClientId())
                .clientSecret(configUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(configUtils.getGoogleRedirectUriLocal())
                .grantType("authorization_code")
                .build();

        try {
            ResponseEntity<String> apiResponseJson = getApiResponseJson(restTemplate, requestParams);

            // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
            String jwtToken = googleLoginResponse.getIdToken();

            // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
            String requestUrl = UriComponentsBuilder.fromHttpUrl(configUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();
            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            if(resultJson != null) {
                GoogleLoginDto googleLoginDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
                OAuth2AccountDto oAuth2AccountDto = oAuth2AccountService.signupWithGoogleOAuth2(googleLoginDto);
                // TODO 암호화 필요 -> 일단 authCode를 보내고, 해당하는 googleLoginDto를 다른 것으로 반환

                response.sendRedirect(configUtils.getFrontendUrlLocal() + "?access_token=" + oAuth2AccountDto.getAccessToken() + "&host_id=" + oAuth2AccountDto.getHostId());
                //httpHeaders.put("body", oAuth2AccountDto.toString());
            }
            else {
                log.error("login failed : no resultJson.");
                response.sendRedirect(configUtils.getFrontendUrlLocal());
            }
        }
        catch (Exception e) {
            log.error("login failed : exception occurs.");
            response.sendRedirect(configUtils.getFrontendUrlLocal());
        }
    }

    @GetMapping(value = "/login/redirect/prod")
    @Operation(summary = "구글 로그인 리다이렉션 페이지(배포용)", description = "구글 로그인 시 코드 반환")
    public void redirectGoogleLoginProd(
            @RequestParam(value = "code") String authCode, HttpServletResponse response) throws IOException {
        // HTTP 통신을 위해 RestTemplate 활용
        RestTemplate restTemplate = new RestTemplate();
        GoogleLoginRequest requestParams = GoogleLoginRequest.builder()
                .clientId(configUtils.getGoogleClientId())
                .clientSecret(configUtils.getGoogleSecret())
                .code(authCode)
                .redirectUri(configUtils.getGoogleRedirectUriProd())
                .grantType("authorization_code")
                .build();

        try {
            // Http Header 설정
            ResponseEntity<String> apiResponseJson = getApiResponseJson(restTemplate, requestParams);

            // ObjectMapper를 통해 String to Object로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // NULL이 아닌 값만 응답받기(NULL인 경우는 생략)
            GoogleLoginResponse googleLoginResponse = objectMapper.readValue(apiResponseJson.getBody(), new TypeReference<GoogleLoginResponse>() {});

            // 사용자의 정보는 JWT Token으로 저장되어 있고, Id_Token에 값을 저장한다.
            String jwtToken = googleLoginResponse.getIdToken();

            // JWT Token을 전달해 JWT 저장된 사용자 정보 확인
            String requestUrl = UriComponentsBuilder.fromHttpUrl(configUtils.getGoogleAuthUrl() + "/tokeninfo").queryParam("id_token", jwtToken).toUriString();
            String resultJson = restTemplate.getForObject(requestUrl, String.class);

            if(resultJson != null) {
                GoogleLoginDto googleLoginDto = objectMapper.readValue(resultJson, new TypeReference<GoogleLoginDto>() {});
                OAuth2AccountDto oAuth2AccountDto = oAuth2AccountService.signupWithGoogleOAuth2(googleLoginDto);
                // TODO 암호화 필요 -> 일단 authCode를 보내고, 해당하는 googleLoginDto를 다른 것으로 반환

                response.sendRedirect(configUtils.getFrontendUrlProd() + "?access_token=" + oAuth2AccountDto.getAccessToken() + "&host_id=" + oAuth2AccountDto.getHostId());
                //httpHeaders.put("body", oAuth2AccountDto.toString());
            }
            else {
                log.error("login failed : no resultJson.");
                response.sendRedirect(configUtils.getFrontendUrlProd());
            }
        }
        catch (Exception e) {
            log.error("login failed : exception occurs.");
            response.sendRedirect(configUtils.getFrontendUrlProd());
        }
    }

    private ResponseEntity<String> getApiResponseJson(RestTemplate restTemplate, GoogleLoginRequest requestParams) {
        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GoogleLoginRequest> httpRequestEntity = new HttpEntity<>(requestParams, headers);
        ResponseEntity<String> apiResponseJson = restTemplate.postForEntity(configUtils.getGoogleAuthUrl() + "/token", httpRequestEntity, String.class);
        return apiResponseJson;
    }

}
