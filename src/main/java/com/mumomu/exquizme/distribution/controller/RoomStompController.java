package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.exception.*;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.stomp.*;
import com.mumomu.exquizme.distribution.web.model.ParticipantCreateForm;
import com.mumomu.exquizme.production.domain.Problem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.jms.DeliveryMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    // TODO HOST말고 Client에도 추가 정보를 제공해야함(다시 접속하라는)
    @MessageMapping("/room/{roomPin}")
    public void joinRoom(@DestinationVariable String roomPin, @Nullable String sessionId) {
        //String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

        // 1. Validation
        try {
            roomService.checkRoomState(roomPin);
            Participant participant = roomService.findParticipantBySessionId(sessionId, roomPin);
            List<ParticipantDto> participantList = roomService.findParticipantsByRoomPin(roomPin).stream().map(ParticipantDto::new).collect(Collectors.toList());
            StompParticipantSignup stompParticipantSignup = new StompParticipantSignup(MessageType.PARTICIPANT, sessionId, participant, participantList, participant.getImageNumber(), participant.getColorNumber());

            messageToAllSubscriber(roomPin, stompParticipantSignup);
        } catch (IllegalAccessException | NullPointerException e) {
            log.error(e.getMessage());
            messageToAllSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, sessionId, e.getMessage()));
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
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        // 1. Validation
        try {
            // 쿠키 -> 세션 ID로 변경(인터셉터에서 처리)
            headerAccessor.setSessionId(sessionId);

            Participant savedParticipant = roomService.joinParticipant(participateForm, roomPin, sessionId);
            StompParticipantSignup stompParticipantSignup = new StompParticipantSignup(MessageType.PARTICIPANT, sessionId, savedParticipant, roomService.findParticipantDtosByRoomPin(roomPin), savedParticipant.getImageNumber(), savedParticipant.getColorNumber());

            messageToAllSubscriber(roomPin, stompParticipantSignup); // 본인에게 세션 아이디를 줘야함.. 근데 이 방법은 안됨.. 일단 닉네임으로 구분해서 받자
        } catch (NullPointerException | IllegalAccessException e) {
            log.error(e.getMessage());
            messageToAllSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, sessionId, e.getMessage()));
        }
    }

    // 퀴즈 시작
    @MessageMapping({"/room/{roomPin}/start"})
    public void startRoom(@DestinationVariable String roomPin) {
        try {
            Problem problem = roomProgressService.startRoom(roomPin);
            messageToClientSubscriber(roomPin, new StompNewProblemForm(MessageType.NEW_PROBLEM, null, problem));
        } catch (InvalidRoomAccessException e) {
            log.error(e.getMessage());
            messageToClientSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, null, e.getMessage()));
        }
    }

    // 퀴즈 정지(리더보딩 및 해설)
    @MessageMapping({"/room/{roomPin}/stop"})
    public void stopRoom(@DestinationVariable String roomPin) {
        try {
            messageToClientSubscriber(roomPin, new StompStopMessage(MessageType.STOP));
        } catch (InvalidRoomAccessException e) {
            log.error(e.getMessage());
            messageToClientSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, null, e.getMessage()));
        }
    }

    // 다음 문제
    @MessageMapping("/room/{roomPin}/next")
    public void nextProblem(@DestinationVariable String roomPin) {
        try {
            Problem problem = roomProgressService.nextProblem(roomPin);
            messageToClientSubscriber(roomPin, new StompNewProblemForm(MessageType.NEW_PROBLEM, null, problem));
        } catch (InvalidRoomAccessException | NoMoreProblemException e) {
            log.error(e.getMessage());
            messageToClientSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, null, e.getMessage()));
        }
    }

    // 정답 제출
    // OX 퀴즈의 경우 방 시간이 끝났을 때 마지막 위치를 통해 정답 제출
    @MessageMapping("/room/{roomPin}/submit")
    public void submitAnswer(@DestinationVariable String roomPin,
                             @RequestBody StompAnswerSubmitForm answerForm) {
        // 1. Validation

        try {
            checkSubmitIsCurrentProblem(roomPin, answerForm.getProblemIdx());
            roomProgressService.updateParticipantInfo(roomPin, answerForm);
            messageToHostSubscriber(roomPin, answerForm);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            messageToHostSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, null, e.getMessage()));
        }
    }

    // OX 퀴즈 - 움직임
    @MessageMapping("/room/{roomPin}/move")
    public void movePlayer(@DestinationVariable String roomPin,
                           @RequestBody StompPlayerMoveForm moveForm) {
        try {
            checkSubmitIsCurrentProblem(roomPin, moveForm.getProblemIdx());
            messageToAllSubscriber(roomPin, moveForm);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            messageToAllSubscriber(roomPin, new StompErrorMessage(MessageType.ERROR, null, e.getMessage()));
        }
    }

    private void messageToClientSubscriber(String roomPin, Object sendMessage) {
        ActiveMQTopic roomTopic = new ActiveMQTopic(PREFIX_TOPIC_NAME + '/' + roomPin);
        sendMessage(roomTopic, sendMessage);
    }

    private void messageToHostSubscriber(String roomPin, Object sendMessage) {
        ActiveMQTopic roomTopic = new ActiveMQTopic(PREFIX_TOPIC_NAME + '/' + roomPin + "/host");
        sendMessage(roomTopic, sendMessage);
    }

    private void messageToAllSubscriber(String roomPin, Object sendMessage) {
        messageToClientSubscriber(roomPin, sendMessage);
        messageToHostSubscriber(roomPin, sendMessage);
    }

    private void sendMessage(ActiveMQTopic roomTopic, Object sendMessage) {
        jmsTemplate.convertAndSend(roomTopic, sendMessage, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });
    }

    private void checkSubmitIsCurrentProblem(String roomPin, int problemIdx) {
        int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();

        if (currentProblemNum != problemIdx) {
            throw new RuntimeException("잘못된 문제 번호입니다.");
        }
    }
}
