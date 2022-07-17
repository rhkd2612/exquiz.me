package com.mumomu.exquizme.distribution.repository;

import com.mumomu.exquizme.distribution.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findRoomById(Long id);
}
