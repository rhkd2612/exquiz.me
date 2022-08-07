package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.ClosedRoomAccessException;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
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

    private final ProblemService problemService;
    private final RoomService roomService;

    @Transactional
    public int updateParticipantInfo(String roomPin, AnswerSubmitForm answerSubmitForm) throws IllegalAccessException {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        Participant targetParticipant = roomService.findParticipantByUuid(answerSubmitForm.getUuid());
        Problem targetProblem = targetRoom.getProblemset().getProblems().get(answerSubmitForm.getProblemIdx());

        int score = 0;
        if(targetProblem.getAnswer().equals(answerSubmitForm.getAnswer()))
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
