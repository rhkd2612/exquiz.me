package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.repository.AnswerRepository;
import com.mumomu.exquizme.distribution.web.dto.AnswerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final RoomService roomService;

    public List<Answer> findAnswerListByProblemIdx(String roomPin, int problemIdx){
        List<Answer> answers = answerRepository.findAnswersByProblemIdx(problemIdx).stream().filter(a ->
                a.getParticipant().getRoom().getPin().equals(roomPin)).collect(Collectors.toList());
        return answers;
    }
}
