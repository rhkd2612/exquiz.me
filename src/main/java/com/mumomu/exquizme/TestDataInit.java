package com.mumomu.exquizme;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomResult;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.production.domain.Host;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.repository.HostRepository;
import com.mumomu.exquizme.production.repository.ProblemsetRepository;
import com.mumomu.exquizme.production.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

//
@Component
@Slf4j
@RequiredArgsConstructor
public class TestDataInit {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final HostRepository hostRepository;
    private final ProblemService problemService;
    private final ProblemsetRepository problemsetRepository;

    // 테스트용 데이터 추가
    @PostConstruct
    @Transactional
    public void init() throws Exception{
        log.info("Test Data Init Complete");
        Room room1 = roomRepository.save(Room.ByBasicBuilder().pin("100000").maxParticipantCount(5).build());
        Room room2 = roomRepository.save(Room.ByBasicBuilder().pin("200000").maxParticipantCount(5).build());

        Participant p1 = participantRepository.save(Participant.ByBasicBuilder().name("홍길동").nickname("홍길동무새").uuid("0aed126c-9b08-4581-b3d3-9630b45c3989").room(room1).build());
        Participant p2 = participantRepository.save(Participant.ByBasicBuilder().name("곽두팔").nickname("곽두팔무새").uuid("1aed126c-9b08-4581-b3d3-9630b45c3989").room(room2).build());

        room1.addParticipant(p1);
        room2.addParticipant(p2);

        Host host = hostRepository.save(Host.builder().name("호스트").nickname("Mumomu").build());
        Problemset problemset = problemService.makeProblemset(host.getId(),"tempTitle","tempDescription","Goodbye Command");
        Problem problem = problemService.makeProblem(problemset.getId(), "MultipleChoiceProblem",1,"tempTitle","tempDescription",30,100,null,"1");

        ProblemOption problemOption = problemService.makeProblemOption(problem.getId(),1,"im first",null);
        ProblemOption problemOption2 = problemService.makeProblemOption(problem.getId(),2,"im second",null);
        ProblemOption problemOption3 = problemService.makeProblemOption(problem.getId(),3,"im third",null);
        ProblemOption problemOption4 = problemService.makeProblemOption(problem.getId(),4,"im fourth",null);
    }
}
