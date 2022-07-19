package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Room;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {
    @Autowired
    RoomService roomService;

    @Test
    void 방생성() throws Exception{
        Room room = roomService.newRoom();
    }

    @Test
    void 생성실패() throws Exception{
        assertThrows(RuntimeException.class, ()->{
            int maxCount = 100;
            while(maxCount-- != 0){
                Room room = roomService.newRoom();
            }
        });
    }
}