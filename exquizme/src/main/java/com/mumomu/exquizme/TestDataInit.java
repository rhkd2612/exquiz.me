package com.mumomu.exquizme;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomResult;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//
@Component
@Slf4j
@RequiredArgsConstructor
public class TestDataInit {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    // 테스트용 데이터 추가
    @PostConstruct
    public void init(){
        log.info("Test Data Init Complete");
        Room room1 = roomRepository.save(Room.builder().pin("100000").maxParticipantCount(5).build());
        Room room2 = roomRepository.save(Room.builder().pin("200000").maxParticipantCount(5).build());

        participantRepository.save(Participant.builder().name("홍길동").nickname("홍길동무새").uuid("0aed126c-9b08-4581-b3d3-9630b45c3989").room(room1).build());
        participantRepository.save(Participant.builder().name("곽두팔").nickname("곽두팔무새").uuid("1aed126c-9b08-4581-b3d3-9630b45c3989").room(room2).build());
    }
}
