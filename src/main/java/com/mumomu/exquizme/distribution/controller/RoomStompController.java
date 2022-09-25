package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.*;
import com.mumomu.exquizme.distribution.service.AnswerService;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.RoomDto;
import com.mumomu.exquizme.distribution.web.dto.stomp.StompAnswerSubmitForm;
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

    // 퀴즈 시작
    @MessageMapping({"/room/{roomPin}/start"})
    public ResponseEntity<?> startRoom(@DestinationVariable String roomPin) {
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.startRoom(roomPin);
            messageToSubscribers(roomPin, new ProblemDto(problem));
            // 3. Make Response
            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.FOUND);
        } catch (InvalidRoomAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 다음 퀴즈
    @MessageMapping("/room/{roomPin}/next")
    public ResponseEntity<?> nextProblem(@DestinationVariable String roomPin) {
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.nextProblem(roomPin);
            messageToSubscribers(roomPin, new ProblemDto(problem));
            // 3. Make Response
            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.FOUND);
        } catch (InvalidRoomAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NoMoreProblemException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.MOVED_PERMANENTLY);
        }
    }

    // 퀴즈방 입장
    // TODO BusinessLogic 서비스로 이동해야함
    // TODO 멘토님께 여쭤봐야함 구조에 대해.. 어떤건 참여자 어떤건 방 Dto 반환함..
    @MessageMapping("/room/{roomPin}")
    public ResponseEntity<?> joinRoom(@DestinationVariable String roomPin,
                                      SimpMessageHeaderAccessor headerAccessor) {
        // 1. Validation
        try {
            // 2. Business Logic
            Room targetRoom = roomService.findRoomByPin(roomPin);
            roomService.checkRoomState(roomPin);

            String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();

            // 3. Make Response
            Participant participant = roomService.findParticipantBySessionId(sessionId, roomPin);

            ParticipantDto participantDto = new ParticipantDto(participant);
            messageToSubscribers(roomPin, participantDto.getNickname());

            return ResponseEntity.ok(participantDto);
        } catch (SessionNotExistException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FOUND);
        } catch (NullPointerException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    // 참가양식 입력
    // TODO 비적절 이름 필터 넣은 후 관련 예외 추가하여야함 + 테스트도
    @MessageMapping("/room/{roomPin}/signup")
    @MessageExceptionHandler(MessageConversionException.class)
    public ResponseEntity<?> signUpParticipant(@DestinationVariable String roomPin,
                                               @RequestBody ParticipantCreateForm participateForm,
                                               SimpMessageHeaderAccessor headerAccessor) {
        // 1. Validation 예정
        try {
            // 쿠키 -> 세션 ID로 변경(인터셉터에서 처리)
            String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
            headerAccessor.setSessionId(sessionId);

            Participant savedParticipant = roomService.joinParticipant(participateForm, roomPin, sessionId);
            ParticipantDto participantDto = new ParticipantDto(savedParticipant);

            messageToSubscribers(roomPin, participantDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(participantDto);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @MessageMapping("/room/{roomPin}/submit")
    public ResponseEntity<?> submitAnswer(@DestinationVariable String roomPin, @RequestBody StompAnswerSubmitForm answerForm) {
        // 1. Validation
        int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();

        if (currentProblemNum != answerForm.getProblemIdx())
            return new ResponseEntity<>("잘못된 문제 번호입니다.", HttpStatus.NOT_ACCEPTABLE);
        try {
            // 2. Business Logic
            int currentScore = roomProgressService.updateParticipantInfo(roomPin, answerForm);
            messageToSubscribers(roomPin, answerForm);
            // 3. Make Response
            return ResponseEntity.ok(currentScore);
        } catch (NullPointerException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private void messageToSubscribers(String roomPin, Object sendMessage) {
        ActiveMQTopic roomTopic = new ActiveMQTopic(PREFIX_TOPIC_NAME + roomPin);

        jmsTemplate.convertAndSend(roomTopic, sendMessage, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });
    }
}
