package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.CreateRandomPinFailureException;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.SessionNotExistException;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.RoomDto;
import com.mumomu.exquizme.distribution.web.model.RoomCreateForm;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 참여에 이용되는 Rest 컨트롤러"})
@RequestMapping("/api/room")
public class RoomRestController {
    private final RoomService roomService;
    private final ProblemService problemService;

    // 퀴즈방 생성
    @PostMapping("/newRoom")
    @Operation(summary = "퀴즈방 생성", description = "새로운 방을 생성합니다(사용자 인증 정보 요구 예정)")
    @ApiResponse(responseCode = "201", description = "방 생성 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 퀴즈 셋")
    @ApiResponse(responseCode = "408", description = "방 생성 실패, 시간 초과(다시 시도 권유)")
    public ResponseEntity<?> newRoom(@RequestBody RoomCreateForm roomCreateForm) {
        // 1. Validation
        try {
            // 2. Business Logic
            Problemset problemset = problemService.getProblemsetById(roomCreateForm.getProblemsetId());
            Room room = roomService.newRoom(problemset, roomCreateForm);

            RoomDto createRoomDto = new RoomDto(room);
            // 3. Make Response
            return new ResponseEntity(createRoomDto, HttpStatus.CREATED);
        } catch (CreateRandomPinFailureException e) {
            log.error("error : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //퀴즈방 열려있는지 확인
    // TODO 도메인에 핀번호가 먼저와도 되나?
    @GetMapping("/{roomPin}/open")
    @Operation(summary = "퀴즈방 조회", description = "방이 존재하는지 조회합니다.")
    @ApiResponse(responseCode = "200", description = "방 조회 성공")
    @ApiResponse(responseCode = "400", description = "가득찬 방 접근 시도")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 방 접근 시도")
    public ResponseEntity<?> findRoom(@PathVariable String roomPin) {
        // 1. Validation
        try {
            // 2. Business Logic
            Room targetRoom = roomService.findRoomByPin(roomPin);
            roomService.checkRoomState(roomPin);
            return ResponseEntity.ok(new RoomDto(targetRoom));
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{roomPin}/close")
    @Operation(summary = "퀴즈방 삭제", description = "기존 방을 삭제합니다(DB에선 삭제되지 않고 PIN 변경)")
    @ApiResponse(responseCode = "302", description = "방 삭제 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 방 삭제 시도")
    public ResponseEntity<?> closeRoom(@PathVariable String roomPin) {
        // 1. Validation
        try {
            // 2. Business Logic
            Room room = roomService.closeRoomByPin(roomPin);
            RoomDto deleteRoomDto = new RoomDto(room);

            // 3. Make Response
            return new ResponseEntity<>(null, HttpStatus.FOUND);
        } catch (NullPointerException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // TODO Pageable 적용해야함.. 왠지 모르겠는데 오류남
    // TODO 쿼리 효율이 좋지 않다. 방을 조회하고, 유저를 조회하여서.. 유저로만 조회할 수 있도록 uuid에 방pin을 붙여도 좋아보인다.
    @GetMapping("/{roomPin}/participants")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @Operation(summary = "방 참여자 목록 조회", description = "해당 방에 참여한 참여자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "참여자 목록 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 방 코드 입력")
    public ResponseEntity<List<ParticipantDto>> printParticipants(@PathVariable String roomPin) {
        try {
            return ResponseEntity.ok(roomService.findParticipantDtosByRoomPin(roomPin));
        } catch (InvalidRoomAccessException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}