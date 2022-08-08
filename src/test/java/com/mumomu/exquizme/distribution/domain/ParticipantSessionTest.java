package com.mumomu.exquizme.distribution.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParticipantSessionTest {
    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    void 외래키를기본키로() throws Exception{
        Participant participant = Participant.ByBasicBuilder().nickname("temp").build();
        ParticipantSession participantSession = ParticipantSession.builder().participant(participant).build();

        em.persist(participantSession);

        Assertions.assertThat(participantSession.getParticipant().getNickname()).isEqualTo("temp");
        Assertions.assertThat(participant).isEqualTo(participantSession.getParticipant());
    }

    //   @Test
//    @Transactional
//    void 복합키생성테스트(){
//        long participantId = 1L;
//        long roomId = 123453L;
//        ParticipantSession participantSession = new ParticipantSession(participantId, roomId);
//
//        em.persist(participantSession);
//        em.flush();
//        em.clear();
//
//        ParticipantSessionKey participantSessionKey = new ParticipantSessionKey();
//        participantSessionKey.setParticipant_id(1L);
//        participantSessionKey.setRoom_id(123453L);
//
//        ParticipantSession find = em.find(ParticipantSession.class, participantSessionKey);
//        Assertions.assertThat(1L).isEqualTo(find.getParticipant_id());
//        Assertions.assertThat(123453L).isEqualTo(find.getRoom_id());
//    }
}