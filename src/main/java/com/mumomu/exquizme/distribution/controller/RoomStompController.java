package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.distribution.service.AnswerService;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
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
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RoomStompController {
    private final RoomService roomService;
    private final RoomProgressService roomProgressService;
    private final AnswerService answerService;
    private final JmsTemplate jmsTemplate;

    private static final String PREFIX_TOPIC_NAME = "room";
    // example : /pub/100000/submit
    // 퀴즈 정답 제출
    @MessageMapping("/room/{roomPin}/submit")
    public ResponseEntity<?> submitAnswer(@DestinationVariable String roomPin, @RequestBody AnswerSubmitForm answerSubmitForm){
        // 1. Validation
        int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();

        if(currentProblemNum != answerSubmitForm.getProblemIdx())
            return new ResponseEntity<>("잘못된 문제 번호입니다.", HttpStatus.NOT_ACCEPTABLE);
        try {
            // 2. Business Logic
            int currentScore = roomProgressService.updateParticipantInfo(roomPin, answerSubmitForm);
            messageToSubscribers(roomPin, answerSubmitForm);
            // 3. Make Response
            return ResponseEntity.ok("성공");
        }catch(NullPointerException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(IllegalStateException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // 퀴즈 시작
    @MessageMapping({"/room/{roomPin}/start"})
//    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    public ResponseEntity<?> startRoom(@DestinationVariable String roomPin){
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.startRoom(roomPin);
            messageToSubscribers(roomPin, new ProblemDto(problem));
            // 3. Make Response
            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.FOUND);
        }catch(InvalidRoomAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 다음 퀴즈
    @MessageMapping("/room/{roomPin}/next")
//    @Operation(summary = "다음 문제 조회", description = "현재 방의 다음 문제를 반환합니다")
//    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
//    @ApiResponse(responseCode = "301", description = "퀴즈 종료")
//    @ApiResponse(responseCode = "302", description = "다음 문제")
//    @ApiResponse(responseCode = "400", description = "존재하지 않는 방 번호 입력")
    public ResponseEntity<?> nextProblem(@DestinationVariable String roomPin){
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.nextProblem(roomPin);
            messageToSubscribers(roomPin, new ProblemDto(problem));
            // 3. Make Response
            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.FOUND);
        }catch(InvalidRoomAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(NoMoreProblemException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.MOVED_PERMANENTLY);
        }
    }

    // TODO 비적절 이름 필터 넣은 후 관련 예외 추가하여야함 + 테스트도
    @MessageMapping("/room/{roomPin}/signup")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path"),
//    })
//    @Operation(summary = "익명사용자 정보 등록 후 방 입장", description = "닉네임(nickname)과 이름(name) 입력 후 방에 입장합니다.")
//    @ApiResponse(responseCode = "201", description = "유저 생성 성공 혹은 기존 유저 정보 변경 -> 방 입장, 사용자 정보 포함")
//    @ApiResponse(responseCode = "400", description = "이름 혹은 닉네임 불충분 혹은 부적절")
//    @ApiResponse(responseCode = "406", description = "이미 존재하는 참가자 정보 혹은 더 이상 참가할 수 없는 방")
    public ResponseEntity<?> signUpParticipant(@DestinationVariable String roomPin, @RequestBody ParticipantCreateForm participateForm,
                                               HttpServletResponse response) {
        // 1. Validation

        try {
            // 2. Business Logic
            Room targetRoom = roomService.findRoomByPin(roomPin);

            // 3. Make Response
            Cookie anonymousCookie = Room.setAnonymousCookie();

            Participant savedParticipant = roomService.joinParticipant(participateForm, targetRoom, UUID.fromString(anonymousCookie.getValue()).toString());
            ParticipantDto participantDto = new ParticipantDto(savedParticipant);

            response.addCookie(anonymousCookie);

            messageToSubscribers(roomPin, participantDto.getNickname());
            return ResponseEntity.status(HttpStatus.CREATED).body(participantDto);
        }catch(NullPointerException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(IllegalAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @MessageMapping("/test")
    public void testMethod(@Payload AnswerSubmitForm answerSubmitForm){
        ActiveMQTopic roomTopic = new ActiveMQTopic("room");

        jmsTemplate.convertAndSend(roomTopic,answerSubmitForm, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });
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
