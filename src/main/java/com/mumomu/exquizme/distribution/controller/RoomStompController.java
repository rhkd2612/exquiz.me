package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.*;
import com.mumomu.exquizme.distribution.service.AnswerService;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.RoomDto;
import com.mumomu.exquizme.distribution.web.dto.stomp.*;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import com.mumomu.exquizme.distribution.web.model.ParticipantCreateForm;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.dto.ProblemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import javax.jms.DeliveryMode;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

@RestController
@Slf4j
@RequiredArgsConstructor
// 웹소켓 API 명세는 노션에 정리(스웨거 사용불가)
public class RoomStompController {
    private final RoomService roomService;
    private final RoomProgressService roomProgressService;
    private final JmsTemplate jmsTemplate;

    private static final String PREFIX_TOPIC_NAME = "room";

    // 퀴즈방 입장
    // TODO BusinessLogic 서비스로 이동해야함
    @MessageMapping("/room/{roomPin}")
    public void joinRoom(@DestinationVariable String roomPin,
                         SimpMessageHeaderAccessor headerAccessor) {
        // 1. Validation
        try {
            roomService.checkRoomState(roomPin);

            String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
            Participant participant = roomService.findParticipantBySessionId(sessionId, roomPin);
            StompParticipantSignup stompParticipantSignup = new StompParticipantSignup(MessageFlag.PARTICIPANT, sessionId, participant);

            messageToHostSubscriber(roomPin, stompParticipantSignup);
        } catch (IllegalAccessException | NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    // 참가양식 입력
    // 이 곳에서는 세션 아이디 발급 전이므로 세션 아이디가 존재하지 않는다. 그래서 stomp-message를 사용하지 않음
    // TODO 비적절 이름 필터 넣은 후 관련 예외 추가하여야함 + 테스트도
    @MessageMapping("/room/{roomPin}/signup")
    //@MessageExceptionHandler(MessageConversionException.class)
    public void signUpParticipant(@DestinationVariable String roomPin,
                                               @RequestBody ParticipantCreateForm participateForm,
                                               SimpMessageHeaderAccessor headerAccessor) {
        // 1. Validation
        try {
            // 쿠키 -> 세션 ID로 변경(인터셉터에서 처리)
            String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
            headerAccessor.setSessionId(sessionId);

            Participant savedParticipant = roomService.joinParticipant(participateForm, roomPin, sessionId);
            StompParticipantSignup stompParticipantSignup = new StompParticipantSignup(MessageFlag.PARTICIPANT, sessionId, savedParticipant);

            messageToSubscribers(roomPin, stompParticipantSignup); // 본인에게 세션 아이디를 줘야함.. 근데 이 방법은 안됨.. 일단 닉네임으로 구분해서 받자
            messageToHostSubscriber(roomPin, stompParticipantSignup);
        } catch (NullPointerException | IllegalAccessException e) {
            log.error(e.getMessage());
        }
    }

    // 퀴즈 시작
    @MessageMapping({"/room/{roomPin}/start"})
    public void startRoom(@DestinationVariable String roomPin) {
        try {
            Problem problem = roomProgressService.startRoom(roomPin);
            messageToSubscribers(roomPin, new StompNewProblemForm(MessageFlag.NEW_PROBLEM, null, problem));
        } catch (InvalidRoomAccessException e) {
            log.error(e.getMessage());
        }
    }

    // 다음 문제
    @MessageMapping("/room/{roomPin}/next")
    public void nextProblem(@DestinationVariable String roomPin) {
        try {
            Problem problem = roomProgressService.nextProblem(roomPin);
            messageToSubscribers(roomPin, new StompNewProblemForm(MessageFlag.NEW_PROBLEM, null, problem));
        } catch (InvalidRoomAccessException | NoMoreProblemException e) {
            log.error(e.getMessage());
        }
    }

    // 정답 제출
    // OX 퀴즈의 경우 방 시간이 끝났을 때 마지막 위치를 통해 정답 제출
    @MessageMapping("/room/{roomPin}/submit")
    public void submitAnswer(@DestinationVariable String roomPin,
                             @RequestBody StompAnswerSubmitForm answerForm) {
        // 1. Validation
        if (checkSubmitIsCurrentProblem(roomPin, answerForm.getProblemIdx())) {
            roomProgressService.updateParticipantInfo(roomPin, answerForm);
            messageToHostSubscriber(roomPin, answerForm);
        }
    }

    // OX 퀴즈 - 움직임
    @MessageMapping("/room/{roomPin}/move")
    public void movePlayer(@DestinationVariable String roomPin,
                           @RequestBody StompPlayerMoveForm moveForm) {
        if (checkSubmitIsCurrentProblem(roomPin, moveForm.getProblemIdx())) {
            messageToSubscribers(roomPin, moveForm);
            messageToHostSubscriber(roomPin, moveForm);
        }
    }

    private void messageToSubscribers(String roomPin, Object sendMessage) {
        ActiveMQTopic roomTopic = new ActiveMQTopic(PREFIX_TOPIC_NAME + '/' + roomPin);
        sendMessage(roomTopic, sendMessage);
    }

    private void messageToHostSubscriber(String roomPin, Object sendMessage) {
        ActiveMQTopic roomTopic = new ActiveMQTopic(PREFIX_TOPIC_NAME + '/' + roomPin + "/host");
        sendMessage(roomTopic, sendMessage);
    }

    private void sendMessage(ActiveMQTopic roomTopic, Object sendMessage) {
        jmsTemplate.convertAndSend(roomTopic, sendMessage, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });
    }

    private boolean checkSubmitIsCurrentProblem(String roomPin, int problemIdx) {
        int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();

        if (currentProblemNum != problemIdx) {
            log.error("잘못된 문제 번호입니다.");
            return false;
        }

        return true;
    }
}
