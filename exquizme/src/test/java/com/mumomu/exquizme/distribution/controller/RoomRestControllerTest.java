package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.service.RoomService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import javax.management.ServiceNotFoundException;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RoomRestControllerTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private RoomService roomService;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @BeforeEach
    public void setUP(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }


    @Test
    void newRoomApiTest() throws Exception{
        mvc.perform(post("/api/room/newRoom"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.pin", notNullValue()));
    }

    @Test
    void closeRoomApiTest() throws Exception {
        String roomPin = "100000";

        mvc.perform(post("/api/room/{roomPin}/close", roomPin))
                .andExpect(status().isAccepted())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.currentState").value("FINISH"));
    }

    @Test
    void closeRoomApiFailureTest() throws Exception {
        String roomPin = "9999999";

        mvc.perform(post("/api/room/{roomPin}/close", roomPin))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void joinRoomApiWithNoCookieTest() throws Exception{
        String roomPin = "100000";

        mvc.perform(get("/api/room/{roomPin}", roomPin))
                .andExpect(status().isMovedPermanently())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.pin").value("100000"));
    }

    @Test
    void joinRoomApiWithCookieTest() throws Exception{
//        String roomPin = "100000";
//        Room room = roomService.findRoomByPin(roomPin);
//
//        Participant participant = Participant.builder().name("test").nickname("test_nickname").uuid(UUID.randomUUID().toString()).room(room).build();
//        when(roomService.joinParticipant(participant)).thenReturn(participant);
//
//        String pUuid = participant.getUuid();

//        mvc.perform(post("/api/room/{roomPin}", roomPin)
//                        .cookie(new Cookie("anonymousCode",pUuid)))
//                .andExpect(status().is())
//                .andDo(MockMvcResultHandlers.print())
//                .andExpect(jsonPath("$.id",notNullValue()))
//                .andExpect(jsonPath("$.pin").value("100000"));
    }

    @Test
    void joinRoomApiFailureTest() throws Exception{
        String roomPin = "9999999";

        mvc.perform(get("/api/room/{roomPin}", roomPin))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void signUpParticipantApiTest() throws Exception{
    }

    @Test
    void printParticipantsApiTest() throws Exception{
    }
}