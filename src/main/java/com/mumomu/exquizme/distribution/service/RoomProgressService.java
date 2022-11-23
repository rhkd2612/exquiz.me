package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.domain.RoomState;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.distribution.repository.AnswerRepository;
import com.mumomu.exquizme.distribution.repository.ParticipantRepository;
import com.mumomu.exquizme.distribution.repository.RoomRepository;
import com.mumomu.exquizme.distribution.web.dto.stomp.StompAnswerSubmitForm;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.dto.ProblemOptionDto;
import com.mumomu.exquizme.production.service.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
    public void updateParticipantInfo(String roomPin, StompAnswerSubmitForm answerSubmitForm) throws IllegalStateException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        Participant targetParticipant = roomService.findParticipantBySessionId(answerSubmitForm.getFromSession(), roomPin);
        Problem targetProblem = targetRoom.getProblemset().getProblems().stream().filter(p -> p.getIdx().equals(answerSubmitForm.getProblemIdx())).findFirst().get();

        targetParticipant.getAnswers().forEach(a -> {
            if(a.getProblemIdx() == answerSubmitForm.getProblemIdx())
                throw new IllegalStateException("이미 답을 낸 문제입니다.");
        });

        Answer answer = Answer.ByBasicBuilder().participant(targetParticipant).problemIdx(answerSubmitForm.getProblemIdx()).answerText(answerSubmitForm.getAnswerText()).build();
        answerRepository.save(answer);
        targetParticipant.submitAnswer(answer);

        log.info("사용자 제출 : " + answerSubmitForm.getAnswerText().toUpperCase());
        log.info("실제 답 : " + targetProblem.getAnswer().toUpperCase());

        if(targetProblem.getAnswer().equalsIgnoreCase(answerSubmitForm.getAnswerText())){
            targetParticipant.updateScore(targetProblem.solve());
        }
        else
            targetProblem.wrong();
    }

    @Transactional
    public Problem startRoom(String roomPin) throws InvalidRoomAccessException {
        Room targetRoom = roomService.findRoomByPin(roomPin);

        for (Problem problem : targetRoom.getProblemset().getProblems())
            problem.reset();

        return getFirstProblem(targetRoom);
    }

    @Transactional
    public Problem getCurrentProblemByPin(String roomPin) throws InvalidRoomAccessException, NoMoreProblemException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        List<Problem> problems = targetRoom.getProblemset().getProblems();

        if(targetRoom.getCurrentProblemNum() + 1 >= problems.size())
            throw new NoMoreProblemException("문제셋에 남은 문제가 없습니다.");

        return problems.stream().filter(
                p -> p.getIdx().equals(targetRoom.getCurrentProblemNum() + 1)).findFirst().get();
    }

    @Transactional
    public Problem nextProblem(String roomPin) throws NoMoreProblemException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        return nextProblem(targetRoom);
    }

    @Transactional
    public Problem getFirstProblem(Room room) throws InvalidRoomAccessException {
        if(room.getCurrentState() != RoomState.READY)
            throw new InvalidRoomAccessException("해당하는 시작 대기 중인 방이 없습니다.");

        room.setCurrentState(RoomState.PLAY);
        room.setCurrentProblemNum(0);

        Problem problem = room.getProblemset().getProblems().stream().filter(
                p -> p.getIdx().equals(room.getCurrentProblemNum())).findFirst().get();

        return problem;
    }

    @Transactional
    public Problem nextProblem(Room room) throws NoMoreProblemException {
        List<Problem> problems = room.getProblemset().getProblems();

        if(room.getCurrentProblemNum() + 1 >= problems.size()){
            room.setCurrentState(RoomState.FINISH);
            throw new NoMoreProblemException("문제셋에 남은 문제가 없습니다.");
        }

        room.setCurrentProblemNum(room.getCurrentProblemNum() + 1);

        return problems.stream().filter(
                p -> p.getIdx().equals(room.getCurrentProblemNum())).findFirst().get();
    }
}
