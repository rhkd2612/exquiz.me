package com.mumomu.exquizme.production.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.production.dto.ProblemOptionSaveDto;
import com.mumomu.exquizme.production.dto.ProblemSaveDto;
import com.mumomu.exquizme.production.dto.ProblemsetSaveDto;
import com.mumomu.exquizme.production.service.ProblemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ProblemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProblemService problemService;

    Long hostId = 1L, problemsetId, problemId;

    @Test @DisplayName("problemset 생성")
    void makeProblemset() throws Exception {
        ProblemsetSaveDto problemsetSaveDto = ProblemsetSaveDto.builder()
                .hostId(hostId)
                .title("Test Problemset Title")
                .description("Test Problemset Description")
                .closingMent("Test Problemset Closing Ment")
                .build();

        MvcResult result = mockMvc.perform(post("/api/problemset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(problemsetSaveDto)))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> data = objectMapper.readValue( //json 파싱
                result.getResponse().getContentAsString(), Map.class);

        Assertions.assertThat(data.get("title")).isEqualTo("Test Problemset Title");
        Assertions.assertThat(data.get("description")).isEqualTo("Test Problemset Description");
        Assertions.assertThat(data.get("closingMent")).isEqualTo("Test Problemset Closing Ment");

        problemsetId = Long.parseLong(data.get("id").toString());
    }



    @Test @DisplayName("problem 생성")
    void makeProblem() throws Exception {
        makeProblemset();

        ProblemSaveDto problemSaveDto = ProblemSaveDto.builder()
                .problemsetId(problemsetId)
                .dtype("MultipleChoiceProblem")
                .index(1)
                .title("Test Problem Title1")
                .description("Test Problem Description")
                .timelimit(10)
                .score(20)
                .picture(null)
                .answer("1")
                .build();

        MvcResult result = mockMvc.perform(post("/api/problem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(problemSaveDto)))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> data = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);

        Assertions.assertThat(data.get("dtype")).isEqualTo("MultipleChoiceProblem");
        Assertions.assertThat(data.get("title")).isEqualTo("Test Problem Title1");
        Assertions.assertThat(data.get("score")).isEqualTo(20);

        problemId = Long.parseLong(data.get("id").toString());
    }

    @Test @DisplayName("problem option 생성")
    void makeProblemOption() throws Exception {
        makeProblem();

        ProblemOptionSaveDto problemOptionSaveDto = ProblemOptionSaveDto.builder()
                .problemId(problemId)
                .index(1)
                .description("Test Problem Option Description")
                .picture(null)
                .build();

        MvcResult result = mockMvc.perform(post("/api/problem_option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(problemOptionSaveDto)))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> data = objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class);

        Assertions.assertThat(data.get("index")).isEqualTo(1);
        Assertions.assertThat(data.get("description")).isEqualTo("Test Problem Option Description");
        Assertions.assertThat(data.get("pickcount")).isEqualTo(0);
    }

    @Test
    void findProblemsets() {
    }

    @Test
    void findProblems() {
    }

    @Test
    void findProblemOptions() {
    }

    @Test
    void updateProblemset() {
    }

    @Test
    void updateProblem() {
    }

    @Test
    void updateProblemOption() {
    }

    private <T> String toJson(T data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}