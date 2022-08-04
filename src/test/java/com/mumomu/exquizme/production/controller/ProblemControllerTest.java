package com.mumomu.exquizme.production.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.production.dto.*;
import com.mumomu.exquizme.production.service.ProblemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    Long hostId = 1L, problemsetId, problemId, problemOptionId;

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
                .andExpect(jsonPath("$.title", is("Test Problemset Title")))
                .andExpect(jsonPath("$.description", is("Test Problemset Description")))
                .andExpect(jsonPath("$.closingMent", is("Test Problemset Closing Ment")))
                .andReturn();

        problemsetId = Long.parseLong(objectMapper.readValue( //json 파싱 후 id 알아내기
                result.getResponse().getContentAsString(), Map.class).get("id").toString());
    }



    @Test @DisplayName("problem 생성")
    void makeProblem() throws Exception {
        makeProblemset();

        ProblemSaveDto problemSaveDto = ProblemSaveDto.builder()
                .problemsetId(problemsetId)
                .dtype("MultipleChoiceProblem")
                .idx(1)
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
                .andExpect(jsonPath("$.dtype", is("MultipleChoiceProblem")))
                .andExpect(jsonPath("$.title", is("Test Problem Title1")))
                .andExpect(jsonPath("$.score", is(20)))
                .andReturn();

        problemId = Long.parseLong(objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class).get("id").toString());
    }

    @Test @DisplayName("problem option 생성")
    void makeProblemOption() throws Exception {
        makeProblem();

        ProblemOptionSaveDto problemOptionSaveDto = ProblemOptionSaveDto.builder()
                .problemId(problemId)
                .idx(1)
                .description("Test Problem Option Description")
                .picture(null)
                .build();

        MvcResult result = mockMvc.perform(post("/api/problem_option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(problemOptionSaveDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idx", is(1)))
                .andExpect(jsonPath("$.description", is("Test Problem Option Description")))
                .andExpect(jsonPath("$.pickcount", is(0)))
                .andReturn();

        problemOptionId = Long.parseLong(objectMapper.readValue(
                result.getResponse().getContentAsString(), Map.class).get("id").toString());
    }

    @Test
    void findProblemsets() throws Exception {
        makeProblemset();

        mockMvc.perform(get("/api/problemsets/{hostId}", hostId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].title", is("Test Problemset Title")))
                .andReturn();
    }



    @Test
    void findProblems() throws Exception {
        makeProblem();

        mockMvc.perform(get("/api/problems/{problemsetId}", problemsetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", is("Test Problem Title1")))
                .andReturn();
    }

    @Test
    void findProblemOptions() throws Exception {
        makeProblemOption();

        mockMvc.perform(get("/api/problem_options/{problemId}", problemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", is("Test Problem Option Description")))
                .andReturn();
    }

    @Test
    void updateProblemset() throws Exception {
        makeProblemset();

        ProblemsetModifyDto problemsetModifyDto = ProblemsetModifyDto.builder()
                .problemsetId(problemsetId)
                .title("Updated Problemset Title")
                .description("Updated Problemset Description")
                .closingMent("Updated Problemset Closing Ment")
                .build();

        mockMvc.perform(put("/api/problemset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(problemsetModifyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Problemset Title")))
                .andReturn();
    }

    @Test
    void updateProblem() throws Exception {
        makeProblem();

        ProblemModifyDto problemModifyDto = ProblemModifyDto.builder()
                .problemId(problemId)
                .dtype("MultipleChoiceProblem")
                .idx(1)
                .title("Updated Problem Title")
                .description("Updated Problem Description")
                .timelimit(50)
                .score(100)
                .picture(null)
                .answer("2")
                .build();

        mockMvc.perform(put("/api/problem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(problemModifyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Problem Title")))
                .andExpect(jsonPath("$.score", is(100)))
                .andReturn();
    }

    @Test
    void updateProblemOption() throws Exception {
        makeProblemOption();

        ProblemOptionModifyDto problemOptionModifyDto = ProblemOptionModifyDto.builder()
                .problemOptionId(problemOptionId)
                .idx(1)
                .description("Updated Problem Option Description")
                .picture(null)
                .build();

        mockMvc.perform(put("/api/problem_option")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(problemOptionModifyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Updated Problem Option Description")))
                .andReturn();
    }

    @Test
    void deleteProblemset() throws Exception {
        makeProblemset();

        mockMvc.perform(delete("/api/problemset/{problemsetId}", problemsetId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void deleteProblem() throws Exception {
        makeProblem();

        mockMvc.perform(delete("/api/problem/{problemId}", problemId))
                .andExpect(status().isOk())
                .andReturn();
    }

    private <T> String toJson(T data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}