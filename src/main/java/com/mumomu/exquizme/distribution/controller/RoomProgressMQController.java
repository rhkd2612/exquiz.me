package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.distribution.service.AnswerService;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.dto.ProblemDto;
import com.mumomu.exquizme.production.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.DeliveryMode;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 진행에 이용되는 컨트롤러(ActiveMQ기반)"})
@RequestMapping("/api/room/{roomPin}/mq")
public class RoomProgressMQController {
    private final RoomService roomService;
    private final RoomProgressService roomProgressService;
    private final AnswerService answerService;
    private final JmsTemplate jmsTemplate;

    // 퀴즈 시작
    @GetMapping("/start")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    public ResponseEntity<?> startRoom(@PathVariable String roomPin){
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.startRoom(roomPin);
            // 3. Make Response
            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.FOUND);
        }catch(InvalidRoomAccessException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 다음 퀴즈
    @GetMapping("/next")
    @Operation(summary = "다음 문제 조회", description = "현재 방의 다음 문제를 반환합니다")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "301", description = "퀴즈 종료")
    @ApiResponse(responseCode = "302", description = "다음 문제")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 방 번호 입력")
    public ResponseEntity<?> nextProblem(@PathVariable String roomPin){
        // 1. Validation
        try {
            // 2. Business Logic
            Problem problem = roomProgressService.nextProblem(roomPin);
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

    // 퀴즈 정답 제출
    @PostMapping("/submit")
    @Operation(summary = "정답 제출", description = "참여자가 퀴즈 정답을 제출합니다.")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "200", description = "정답 제출 성공")
    @ApiResponse(responseCode = "400", description = "퀴즈 없음")
    @ApiResponse(responseCode = "406", description = "현재 진행중이 아닌 문제 답이거나 이미 제출한 이력이 있을 경우")
    public ResponseEntity<?> submitAnswer(@PathVariable String roomPin, @RequestBody AnswerSubmitForm answerSubmitForm){
        // TODO 방 닫힐 때 topic 제거해주어야함 혹은 비워주기
        ActiveMQTopic roomTopic = new ActiveMQTopic("room" + roomPin);

        // 1. Validation
        int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();

        if(currentProblemNum != answerSubmitForm.getProblemIdx())
            return new ResponseEntity<>("잘못된 문제 번호입니다.", HttpStatus.NOT_ACCEPTABLE);
        try {
            // 2. Business Logic
            int currentScore = roomProgressService.updateParticipantInfo(roomPin, answerSubmitForm);

            jmsTemplate.convertAndSend(roomTopic, answerSubmitForm, message -> {
                message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                message.setJMSCorrelationID(UUID.randomUUID().toString());
                message.setJMSPriority(10);
                return message;
            });
            // 3. Make Response
            return ResponseEntity.ok(currentScore);
        }catch(NullPointerException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(IllegalStateException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // TODO DTO로 변경해야함
    @GetMapping("/submitList")
    @Operation(summary = "이번문제 정답제출 리스트", description = "방의 현재 문제의 제출 결과를 반환합니다.")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "퀴즈 없음")
    public ResponseEntity<?> submitList(@PathVariable String roomPin){
        try {
            int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();
            return ResponseEntity.ok(answerService.findAnswerListByProblemIdx(roomPin, currentProblemNum));
        }catch(InvalidRoomAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "리더보드", description = "방의 지금까지 점수 리스트를 반환합니다.(내림차순)")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "퀴즈 없음")
    public ResponseEntity<?> leaderboard(@PathVariable String roomPin){
        try {
            List<ParticipantDto> resultLeaderboard = roomService.findParticipantsByRoomPin(roomPin).stream().map(p -> new ParticipantDto(p)).collect(Collectors.toList());

            Collections.sort(resultLeaderboard,
                    (p1, p2) -> p2.getCurrentScore() - p1.getCurrentScore());

            return ResponseEntity.ok(resultLeaderboard);
        }catch(InvalidRoomAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
