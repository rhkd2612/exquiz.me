package com.mumomu.exquizme.distribution.service;

import com.mumomu.exquizme.distribution.domain.Answer;
import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.web.dto.AnswerList;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.stomp.MessageType;
import com.mumomu.exquizme.distribution.web.dto.stomp.StompAnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnswerService {
    private final RoomProgressService roomProgressService;
    private final RoomService roomService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public AnswerList findAnswerListByProblemIdx(String roomPin) {
        log.info("AnswerListDto 생성");
        AnswerList answerList = new AnswerList();
        Room targetRoom = roomService.findRoomByPin(roomPin);
        String answer = targetRoom.getProblemset().getProblems().stream().filter(p -> p.getIdx().equals(targetRoom.getCurrentProblemNum())).findFirst().get().getAnswer();
        List<Participant> participants = roomService.findParticipantsByRoomPin(roomPin);

        log.info("현재 문제 정답 : " + answer);

        for (Participant p : participants) {
            if (p.getAnswers().isEmpty() || p.getAnswers().size() <= targetRoom.getCurrentProblemNum()) {
                log.info("사용자 미제출 : " + p.getNickname());
                roomProgressService.updateParticipantInfo(roomPin, new StompAnswerSubmitForm(MessageType.ANSWER, p.getSessionId(), targetRoom.getCurrentProblemNum(), ""));
                answerList.addParticipant(new ParticipantDto(p, false));
                continue;
            }

            Answer curUserAnswer = p.getAnswers().stream().filter(
                    a -> a.getProblemIdx() == targetRoom.getCurrentProblemNum()
            ).findFirst().get();

            log.info("사용자 제출 정답 " + curUserAnswer.getAnswerText() + " : " + p.getNickname());
            answerList.addParticipant(new ParticipantDto(p, curUserAnswer.getAnswerText().equalsIgnoreCase(answer)));
        }

        answerList.sortParticipantByScore();

        log.info("AnswerListDto 반환");
        return answerList;
    }
}
