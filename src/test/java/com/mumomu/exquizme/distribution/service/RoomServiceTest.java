package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.service.ProblemService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.Part;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    RoomService roomService;
    @Autowired
    ProblemService problemService;

    Room room;
    Room room2;
    @BeforeEach
    void setUp(){
        room = roomService.newRoom(1L);
        room2 = roomService.newRoom(1L);
    }

    @AfterEach
    void setDown(){
        roomService.closeRoomByPin(room.getPin());
        roomService.closeRoomByPin(room2.getPin());
    }

    @Test
    @Transactional
    @DisplayName("방생성성공")
    void createRoom() throws Exception{
        em.persist(room);
        Room savedRoom = em.find(Room.class, room.getId());

        assertThat(room).isEqualTo(savedRoom);
    }

    @Test
    @Transactional
    @DisplayName("방핀번호조회")
    void findRoomByRoomPin(){
        Room findRoomByPin = roomService.findRoomByPin(room.getPin());

        assertThat(room).isEqualTo(findRoomByPin);
    }

    @Test
    @Transactional
    @DisplayName("방아이디조회")
    void findRoomByRoomId(){
        Room findRoomById= roomService.findRoomById(room.getId());

        assertThat(room).isEqualTo(findRoomById);
    }

    @Test
    @Transactional
    @DisplayName("방생성실패")
    void createRoomFailure() throws Exception{
        assertThrows(RuntimeException.class, ()->{
            int maxCount = 51;
            while(maxCount-- != 0){
                roomService.newRoom(1L);
            }
        });
    }

    @Test
    @Transactional
    @DisplayName("존재하지않는방조회")
    void findInvalidRoom(){
        assertThrows(NullPointerException.class, ()-> {
            roomService.findRoomByPin("111111");
        });
    }

    @Test
    @Transactional
    @DisplayName("방참여")
    void participateRoom(){
        Participant participant = Participant.builder().nickname("userA").uuid(UUID.randomUUID().toString()).room(room).build();
        Participant savedParticipant = roomService.joinParticipant(participant);

        assertThat(room).isEqualTo(savedParticipant.getRoom());
    }

    @Test
    @Transactional
    @DisplayName("방에참여한참여자목록조회")
    void findParticipantsListInRoom(){
        Participant participant = Participant.builder().nickname("userA").uuid(UUID.randomUUID().toString()).room(room).build();
        Participant participant2 = Participant.builder().nickname("userB").uuid(UUID.randomUUID().toString()).room(room).build();
        Participant participant3 = Participant.builder().nickname("userC").uuid(UUID.randomUUID().toString()).room(room2).build();

        roomService.joinParticipant(participant);
        roomService.joinParticipant(participant2);
        roomService.joinParticipant(participant3);

        assertThat(roomService.findParticipantsByRoomPin(room.getPin()).size()).isEqualTo(2);
        assertThat(roomService.findParticipantsByRoomPin(room2.getPin()).size()).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("방삭제")
    void closeRoom(){
        String pin = room.getPin();

        Room roomByPin = roomService.closeRoomByPin(pin);
    }

    @Test
    @Transactional
    @DisplayName("방삭제후재삭제")
    void closeSameRoomTwice(){
        String pin = room.getPin();

        Room roomByPin = roomService.closeRoomByPin(pin);

        assertThrows(NullPointerException.class, ()-> {
            Room roomByPin2 = roomService.findRoomByPin(pin);
        });
    }

    @Test
    @Transactional
    @DisplayName("익명사용자입장")
    public void joinAnonymousUser(){
        Participant participant =
                Participant.builder().name("test").nickname("tester").uuid(UUID.randomUUID().toString()).build();
        Participant anonymous = roomService.joinParticipant(participant);
        assertThat(anonymous).isEqualTo(participant);
    }

    @Test
    @Transactional
    @DisplayName("익명사용자재입장")
    public void joinSameAnonymousUserTwice(){
        Participant participant =
                Participant.builder().name("test").nickname("tester").uuid(UUID.randomUUID().toString()).build();

        Participant anonymous = roomService.joinParticipant(participant);
        Participant anonymous2 = roomService.joinParticipant(anonymous);
        assertThat(anonymous).isEqualTo(anonymous2);
        assertThat(participant).isEqualTo(anonymous2);
    }

    @Test
    @Transactional
    @DisplayName("두익명사용자입장")
    public void joinTwoAnonymousUser(){
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