package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import org.assertj.core.api.Assertions;
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

    @Test
    @Transactional
    void 방생성성공() throws Exception{
        Room room = roomService.newRoom();

        em.persist(room);
        Room savedRoom = em.find(Room.class, room.getId());

        assertThat(room).isEqualTo(savedRoom);
    }

    @Test
    @Transactional
    void 방핀번호조회(){
        Room room = roomService.newRoom();
        Room findRoomByPin = roomService.findRoomByPin(room.getPin());

        assertThat(room).isEqualTo(findRoomByPin);
    }

    @Test
    @Transactional
    void 방아이디조회(){
        Room room = roomService.newRoom();
        Room findRoomById= roomService.findRoomById(room.getId());

        assertThat(room).isEqualTo(findRoomById);
    }



    @Test
    @Transactional
    void 방생성실패() throws Exception{
        assertThrows(RuntimeException.class, ()->{
            int maxCount = 100;
            while(maxCount-- != 0){
                Room room = roomService.newRoom();
            }
        });
    }

    @Test
    @Transactional
    void 존재하지않는방조회(){
        assertThrows(NullPointerException.class, ()-> {
            roomService.findRoomByPin("111111");
        });
    }

    @Test
    @Transactional
    void 방참여(){
        Room room = roomService.newRoom();
        Participant participant = Participant.builder().nickname("userA").uuid(UUID.randomUUID().toString()).room(room).build();
        Participant savedParticipant = roomService.joinParticipant(participant);

        assertThat(room).isEqualTo(savedParticipant.getRoom());
    }

    @Test
    @Transactional
    void 방에참여한참여자목록조회(){
        Room room = roomService.newRoom();
        Room room2 = roomService.newRoom();

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
    void 방삭제(){
        Room room = roomService.newRoom();
        String pin = room.getPin();

        Room roomByPin = roomService.closeRoomByPin(pin);
    }

    @Test
    @Transactional
    void 방삭제후재삭제(){
        Room room = roomService.newRoom();
        String pin = room.getPin();

        Room roomByPin = roomService.closeRoomByPin(pin);

        assertThrows(NullPointerException.class, ()-> {
            Room roomByPin2 = roomService.findRoomByPin(pin);
        });
    }
}