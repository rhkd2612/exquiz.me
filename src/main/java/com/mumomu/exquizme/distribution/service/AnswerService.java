package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.repository.AnswerRepository;
import com.mumomu.exquizme.distribution.web.dto.AnswerListDto;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.production.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final RoomService roomService;

    @Transactional
    public AnswerListDto findAnswerListByProblemIdx(String roomPin, int problemIdx){
        AnswerListDto answerListDto = new AnswerListDto();

        List<Answer> submits = answerRepository.findAnswersByProblemIdx(problemIdx).stream().filter(a ->
                a.getParticipant().getRoom().getPin().equals(roomPin)).collect(Collectors.toList());

        Room targetRoom = roomService.findRoomByPin(roomPin);
        String answer = targetRoom.getProblemset().getProblems().get(targetRoom.getCurrentProblemNum()).getAnswer();

        for (Answer curSubmit : submits) {
            answerListDto.addParticipant(new ParticipantDto(curSubmit.getParticipant()),curSubmit.getAnswerText().equals(answer));
        }

        return answerListDto;
    }
}
