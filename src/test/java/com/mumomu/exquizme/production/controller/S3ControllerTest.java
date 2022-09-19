package com.mumomu.exquizme.production.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class S3ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test @DisplayName("파일 시스템 이미지 업로드")
    void uploadByFileSystem() throws Exception {
        //TODO : 파일 경로 상대경로로 수정
        MockMultipartFile image = new MockMultipartFile("file",
                "KakaoTalk_Photo_2022-08-18-23-27-46.png", "image/png",
                new FileInputStream("/Users/minkyumkim/Downloads/KakaoTalk_Photo_2022-08-18-23-27-46.png"));

        mockMvc.perform(multipart("/api/image/upload/file")
                .file(image))
                .andExpect(status().is2xxSuccessful());
    }

    @Test @DisplayName("url 이미지 업로드")
    void uploadByUrl() throws Exception {
        mockMvc.perform(post("/api/image/upload")
                .contentType(MediaType.APPLICATION_JSON)
                .param("url", "https://exquiz-me-s3.s3.ap-northeast-2.amazonaws.com/static/ddd.png"))
                .andExpect(status().is2xxSuccessful());
    }
}