package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomState;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final ProblemService problemService;

    // application-XXX.yml 파일 local-100000 prod-100000, test-100000
    @Value("${min.pin.value}")
    private String MIN_PIN_VALUE;

    // application-XXX.yml 파일 local-100500 prod-999999, test-100005
    @Value("${max.pin.value}")
    private String MAX_PIN_VALUE;

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

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
    public Room newRoom(Long problemsetId){
        Problemset roomProblemset = problemService.getProblemsetById(problemsetId);
        return newRoomLogic(roomProblemset);
    }

    @Transactional
    public Room newRoom(Problemset roomProblemset){
        return newRoomLogic(roomProblemset);
    }

    private String getRandomPin() {
        System.out.println(MIN_PIN_VALUE + ' ' + MAX_PIN_VALUE);

        int max = Integer.valueOf(MAX_PIN_VALUE);
        int min = Integer.valueOf(MIN_PIN_VALUE);

        return Integer.toString((int) (Math.random() * (max - min)) + min);
    }

    @Transactional(readOnly = true)
    public Participant findParticipantByUuid(String uuid){
        Optional<Participant> optParticipant = participantRepository.findByUuid(uuid);

        if(optParticipant.isEmpty())
            throw new NullPointerException("쿠키가 존재하지 않습니다.");

        return optParticipant.get();
    }

    @Transactional(readOnly = true)
    public Room findRoomById(Long roomId){
        Optional<Room> optRoom = roomRepository.findRoomById(roomId);

        if(optRoom.isEmpty())
            throw new NullPointerException("존재하지 않는 방입니다.");

        return roomRepository.findRoomById(roomId).get();
    }

    @Transactional(readOnly = true)
    public Room findRoomByPin(String roomPin){
        Optional<Room> optRoom = roomRepository.findRoomByPin(roomPin);

        if(optRoom.isEmpty() || optRoom.get().getCurrentState() == RoomState.FINISH)
            throw new NullPointerException("존재하지 않는 방입니다.");

        return optRoom.get();
    }

    @Transactional
    public Room closeRoomByPin(String roomPin){
        Optional<Room> optRoom = roomRepository.findRoomByPin(roomPin);

        if(optRoom.isEmpty())
            throw new NullPointerException("존재하지 않는 방입니다.");

        Room targetRoom = optRoom.get();

        targetRoom.setEndDate(new Date());
        targetRoom.setPin(formatter.format(targetRoom.getEndDate()) + targetRoom.getPin());
        targetRoom.setCurrentState(RoomState.FINISH);

        return targetRoom;
    }

    // TODO null처리
    @Transactional(readOnly = true)
    public List<ParticipantDto> findParticipantsByRoomPin(String roomPin){
        Room room = roomRepository.findRoomByPin(roomPin).get();
        return participantRepository.findAllByRoom(room).stream().map(p -> new ParticipantDto(p)).collect(Collectors.toList());
    }

    private Room newRoomLogic(Problemset roomProblemset) {
        String randomPin;
        Optional<Room> targetRoom;
        int retryCount = 10; // 최대 try 횟수, 무한 루프 방지

        do{
            randomPin = getRandomPin(); //MIN_PIN_RANGE ~ MAX_PIN_RANGE 랜덤 숫자 생성
            targetRoom = roomRepository.findRoomByPin(randomPin);
            retryCount--;
        }
        while(!targetRoom.isEmpty() && retryCount > 0);

        if(retryCount == 0){
            throw new RuntimeException("다시 시도 해주세요.");
        }

        Room room = Room.builder().pin(randomPin).startDate(new Date()).currentState(RoomState.READY).problemset(roomProblemset).build();
        log.info("random Pin is {}",randomPin);

        return roomRepository.save(room);
    }
}
