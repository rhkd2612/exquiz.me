package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.exception.ClosedRoomAccessException;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.NoMoreProblemException;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 진행에 이용되는 컨트롤러"})
@RequestMapping("/api/room/{roomPin}")
public class RoomProgressController {
    private final RoomService roomService;
    private final RoomProgressService roomProgressService;
    private final ProblemService problemService;

    // 퀴즈 시작
    @PostMapping("/start")
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
    public ResponseEntity<?> submitAnswer(@PathVariable String roomPin, @RequestBody AnswerSubmitForm answerSubmitForm){
        // 1. Validation
        try {
            // 2. Business Logic
            int currentScore = roomProgressService.updateParticipantInfo(roomPin, answerSubmitForm);
            // 3. Make Response
            return ResponseEntity.ok(currentScore);
        }catch(NullPointerException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
