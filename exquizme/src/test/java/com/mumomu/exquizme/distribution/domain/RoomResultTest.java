package com.mumomu.exquizme.distribution.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RoomResultTest {
    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    void 외래키를기본키로() throws Exception{
        Room room = Room.builder().pin(111111).build();
        RoomResult roomResult = RoomResult.builder().room(room).build();

        em.persist(roomResult);

        Assertions.assertThat(roomResult.getRoom().getPin()).isEqualTo(111111);
        Assertions.assertThat(room).isEqualTo(roomResult.getRoom());
    }
}