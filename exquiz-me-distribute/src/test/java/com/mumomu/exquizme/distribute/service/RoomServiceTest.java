package com.mumomu.exquizme.distribute.service;

import com.mumomu.exquizme.distribute.domain.Participant;
import com.mumomu.exquizme.distribute.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        Participant participant = new Participant();
        participant.setName("test");
        participant.setNickname("tester");
        participant.setUuid(UUID.randomUUID().toString());

        Participant anonymous = roomService.join(participant);
        assertThat(anonymous).isEqualTo(participant);
    }

    @Test
    @Transactional
    public void anonymousConnectTwice(){
        Participant participant = new Participant();
        participant.setName("test");
        participant.setNickname("tester");
        participant.setUuid(UUID.randomUUID().toString());

        Participant anonymous = roomService.join(participant);
        Participant anonymous2 = roomService.join(anonymous);
        assertThat(anonymous).isEqualTo(anonymous2);
        assertThat(participant).isEqualTo(anonymous2);
    }

    @Test
    @Transactional
    public void twoAnonymousConnect(){
        Participant participant = new Participant();
        participant.setName("test");
        participant.setNickname("tester");
        participant.setUuid(UUID.randomUUID().toString());

        Participant participant2 = new Participant();
        participant.setName("test2");
        participant.setNickname("tester2");
        participant.setUuid(UUID.randomUUID().toString());

        Participant anonymous = roomService.join(participant);
        Participant anonymous2 = roomService.join(participant2);

        assertThat(anonymous).isEqualTo(participant);
        assertThat(anonymous2).isEqualTo(participant2);
        assertThat(anonymous).isNotEqualTo(anonymous2);
    }
}