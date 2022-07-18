package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomState;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    private Map<Integer, Room> existRoomCheckMap = new HashMap<>();

    @Transactional
    public Participant joinParticipant(Participant participant){
        Optional<Participant> findParticipant = participantRepository.findByUuid(participant.getUuid());

        if(findParticipant.isEmpty()){
            participantRepository.save(participant);
        }
        else{
            log.info("방문 이력이 있는 사용자입니다.");
            participant = findParticipant.get();
        }

        return participant;
    }

    @Transactional
    public Room newRoom(){
        //100000 ~ 999999 랜덤 숫자 생성
        int randomPin = (int)(Math.random() * 900000) + 100000;
        Room target = existRoomCheckMap.get(randomPin);

        while(target != null || randomPin == 1000000){
            randomPin = (int)(Math.random() * 900000) + 100000;

            if(target != null && target.getCurrentState() == RoomState.FINISH)
                break;
        }

        Room room = Room.builder().pin(randomPin).build();

        existRoomCheckMap.put(randomPin, room);
        log.info("random Pin is {}",randomPin);

        return roomRepository.save(room);
    }

    public Participant findParticipant(String uuid){
        Optional<Participant> findParticipant = participantRepository.findByUuid(uuid);
        return findParticipant.get();
    }

    public Room findRoomById(Long roomId){
        log.info("Find room(room id : " + roomId + ")");
        Room room = roomRepository.findRoomById(roomId).get();

//        if(room == null)
//            throw new RuntimeException("존재하지 않는 방입니다");
        return room;
    }

    public Room findRoomByPin(Long roomPin){
        log.info("Find room(room id : " + roomPin + ")");
        Room room = roomRepository.findRoomByPin(roomPin).get();

//        if(room == null)
//            throw new RuntimeException("존재하지 않는 방입니다");
        return room;
    }

    @Transactional
    public List<ParticipantDto> findParticipantsByRoomPin(Long roomPin){
        Room room = roomRepository.findRoomByPin(roomPin).get();
        return participantRepository.findAllByRoom(room).stream().map(p -> new ParticipantDto(p)).collect(Collectors.toList());
    }
}
