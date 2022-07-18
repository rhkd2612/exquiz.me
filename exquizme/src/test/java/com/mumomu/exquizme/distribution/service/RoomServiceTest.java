package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomServiceTest {
    @Autowired
    RoomService roomService;

    @Test
    void 랜덤방생성(){
        int maxCount = 50000;
        while(maxCount-- != 0){
            Room room = roomService.newRoom();
        }
    }
}