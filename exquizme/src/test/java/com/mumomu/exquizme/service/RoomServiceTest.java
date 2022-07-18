package com.mumomu.exquizme.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.production.domain.Problem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomService roomService;

    @Test
    @Transactional
    public void anonymousConnect(){
        Participant participant =
                Participant.builder().name("test").nickname("tester").uuid(UUID.randomUUID().toString()).build();
        Participant anonymous = roomService.joinParticipant(participant);
        assertThat(anonymous).isEqualTo(participant);
    }

    @Test
    @Transactional
    public void anonymousConnectTwice(){
        Participant participant =
                Participant.builder().name("test").nickname("tester").uuid(UUID.randomUUID().toString()).build();

        Participant anonymous = roomService.joinParticipant(participant);
        Participant anonymous2 = roomService.joinParticipant(anonymous);
        assertThat(anonymous).isEqualTo(anonymous2);
        assertThat(participant).isEqualTo(anonymous2);
    }

    @Test
    @Transactional
    public void twoAnonymousConnect(){
        Participant participant =
                Participant.builder().name("test").nickname("tester").uuid(UUID.randomUUID().toString()).build();

        Participant participant2 =
                Participant.builder().name("test2").nickname("tester2").uuid(UUID.randomUUID().toString()).build();

        Participant anonymous = roomService.joinParticipant(participant);
        Participant anonymous2 = roomService.joinParticipant(participant2);

        assertThat(anonymous).isEqualTo(participant);
        assertThat(anonymous2).isEqualTo(participant2);
        assertThat(anonymous).isNotEqualTo(anonymous2);
    }
}