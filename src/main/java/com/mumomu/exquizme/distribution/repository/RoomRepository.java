package com.mumomu.exquizme.distribution.repository;

import com.mumomu.exquizme.distribution.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findRoomById(Long id);

    // TODO 종료되지않은 방만 조회해야한다
    Optional<Room> findRoomByPin(String pin);
}
