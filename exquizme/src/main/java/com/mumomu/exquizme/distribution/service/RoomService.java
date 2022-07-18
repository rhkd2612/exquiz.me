package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    private Map<Integer, Boolean> existRoomCheckMap = new HashMap<>();

    @Transactional
    public Participant joinParticipant(Participant participant){
        List<Participant> findParticipant = participantRepository.findByUuid(participant.getUuid());

        if(findParticipant.isEmpty()){
            participantRepository.save(participant);
        }
        else{
            log.info("방문 이력이 있는 사용자입니다.");
            participant = findParticipant.get(0);
        }

        return participant;
    }

    @Transactional
    public Room newRoom(){
        //100000 ~ 999999 랜덤 숫자 생성
        int randomPin = (int)(Math.random() * 900000) + 100000;

        while(existRoomCheckMap.get(randomPin) != null || randomPin == 1000000){
            randomPin = (int)(Math.random() * 900000) + 100000;
        }

        existRoomCheckMap.put(randomPin, true);
        log.info("random Pin is {}",randomPin);

        return roomRepository.save(Room.builder().pin(randomPin).build());
    }

    public Participant findParticipant(String uuid){
        List<Participant> findParticipant = participantRepository.findByUuid(uuid);
        return findParticipant.get(0);
    }

    public Room findRoomById(Long roomId){
        log.info("Find room(room id : " + roomId + ")");
        Room room = roomRepository.findRoomById(roomId);

//        if(room == null)
//            throw new RuntimeException("존재하지 않는 방입니다");
        return room;
    }

    public Room findRoomByPin(Long roomPin){
        log.info("Find room(room id : " + roomPin + ")");
        Room room = roomRepository.findRoomByPin(roomPin);

//        if(room == null)
//            throw new RuntimeException("존재하지 않는 방입니다");
        return room;
    }
}
