package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.distribution.repository.AnswerRepository;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomProgressService {
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final AnswerRepository answerRepository;

    private final ProblemService problemService;
    private final RoomService roomService;

    @Transactional
    public int updateParticipantInfo(String roomPin, AnswerSubmitForm answerSubmitForm) throws IllegalStateException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        Participant targetParticipant = roomService.findParticipantBySessionId(answerSubmitForm.getSessionId(), roomPin);
        Problem targetProblem = targetRoom.getProblemset().getProblems().get(answerSubmitForm.getProblemIdx());

        targetParticipant.getAnswers().forEach(a -> {
            if(a.getProblemIdx() == answerSubmitForm.getProblemIdx())
                throw new IllegalStateException("이미 답을 낸 문제입니다.");
        });

        Answer answer = Answer.ByBasicBuilder().participant(targetParticipant).problemIdx(answerSubmitForm.getProblemIdx()).answerText(answerSubmitForm.getAnswerText()).build();
        answerRepository.save(answer);

        targetParticipant.getAnswers().add(answer);

        int score = 0;
        if(targetProblem.getAnswer().equals(answerSubmitForm.getAnswerText()))
            score = targetProblem.solve();
        else
            targetProblem.wrong();
        return targetParticipant.updateParticipantInfo(score);
    }

    @Transactional
    public Problem startRoom(String roomPin) throws InvalidRoomAccessException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        return targetRoom.startRoom();
    }

    @Transactional
    public Problem nextProblem(String roomPin) throws NoMoreProblemException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        return targetRoom.nextProblem();
    }
}
