package com.mumomu.exquizme.distribute.service;

import com.mumomu.exquizme.distribute.domain.Participant;
import com.mumomu.exquizme.distribute.repository.ParticipantRepository;
import com.mumomu.exquizme.distribute.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public Participant join(Participant participant){
        System.out.println(participant.getUuid());
        List<Participant> findParticipant = participantRepository.findByUuid(participant.getUuid());
        System.out.println(findParticipant);

        if(findParticipant.isEmpty()) {
            participantRepository.save(participant);
        } else{
            log.info("방문 이력이 있는 사용자입니다.");
            participant = findParticipant.get(0);
        }

        return participant;
    }
}
