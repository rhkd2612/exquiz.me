package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public Participant join(Participant participant){
        List<Participant> findParticipant = participantRepository.findByUuid(participant.getUuid());

        if(findParticipant.isEmpty()) {
            participantRepository.save(participant);
        } else{
            log.info("방문 이력이 있는 사용자입니다.");
            participant = findParticipant.get(0);
        }

        return participant;
    }

    public Room findRoom(Long roomId){
        log.info("Find room(room id : " + roomId + ")");
        Room room = roomRepository.findRoomById(roomId);

        if(room == null)
            throw new RuntimeException("존재하지 않는 방입니다");
        return room;
    }
}
