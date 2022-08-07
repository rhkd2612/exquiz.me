package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.exception.CreateRandomPinFailureException;
import com.mumomu.exquizme.distribution.exception.DuplicateSignUpException;
import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.exception.RoomNotReachableException;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.RoomDto;
import com.mumomu.exquizme.distribution.web.model.ParticipantCreateForm;
import com.mumomu.exquizme.distribution.web.model.RoomCreateForm;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 참여에 이용되는 컨트롤러"})
@RequestMapping("/api/room")
public class RoomRestController {
    private final RoomService roomService;
    private final ProblemService problemService;

    // 퀴즈방 생성
    @PostMapping("/newRoom")
    @Operation(summary = "퀴즈방 생성", description = "새로운 방을 생성합니다(사용자 인증 정보 요구 예정)")
    @ApiResponse(responseCode = "201", description = "방 생성 성공")
    @ApiResponse(responseCode = "400", description = "퀴즈셋 없음")
    @ApiResponse(responseCode = "408", description = "방 생성 실패, 시간 초과(다시 시도 권유)")
    public ResponseEntity<?> newRoom(@RequestBody RoomCreateForm roomCreateForm){
        // 1. Validation
        try {
            // 2. Business Logic
            Problemset problemset = problemService.getProblemsetById(roomCreateForm.getProblemsetId());
            Room room = roomService.newRoom(problemset, roomCreateForm.getMaxParticipantCount());

            RoomDto createRoomDto = new RoomDto(room);
            // 3. Make Response
            return new ResponseEntity(createRoomDto, HttpStatus.CREATED);
        }catch(CreateRandomPinFailureException e){
            log.info("error : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        }catch(NullPointerException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    //퀴즈방 폐쇄
    // TODO 도메인에 핀번호가 먼저와도 되나?
    // pin번호만 보내면 되서 patch mapping을 안해도 되지 않을까? 여쭤봐야될 것 같다
    @PostMapping("/{roomPin}/close")
    @Operation(summary = "퀴즈방 삭제", description = "기존 방을 삭제합니다(DB에선 삭제되지 않고 PIN 변경)")
    @ApiResponse(responseCode = "202", description = "방 삭제 성공")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 방 삭제 시도")
    public ResponseEntity<?> closeRoom(@PathVariable String roomPin){
        // 1. Validation
        try{
            // 2. Business Logic
            Room room = roomService.closeRoomByPin(roomPin);
            RoomDto deleteRoomDto = new RoomDto(room);

            // 3. Make Response
            return new ResponseEntity(deleteRoomDto, HttpStatus.ACCEPTED);
        } catch(NullPointerException e){
            log.info(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // 퀴즈방 입장
    // TODO Cookie에 관한 수정이 필요함 + WebSocket 사용 시 쿠키가 필요없어질수도..?
    // TODO Swagger변경 + API 테스트 필요
    // TODO BusinessLogic 서비스로 이동해야함
    // TODO 멘토님께 여쭤봐야함 구조에 대해.. 어떤건 참여자 어떤건 방 Dto 반환함..
    @GetMapping("/{roomPin}")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @Operation(summary = "퀴즈방 조회", description = "쿠키가 있는지 확인 후 존재 시 방 입장(참여자 Dto 반환), 미 존재 시 등록 화면으로 이동합니다.(방 Dto 반환)")
    @ApiResponse(responseCode = "200", description = "방 입장 성공(기존 쿠키 정보를 토대로 입장 - 참여자 Dto 반환)")
    @ApiResponse(responseCode = "302", description = "방 입장 실패(사용자 정보 입력 필요 -> 사용자 이름/닉네임 등록 씬으로 입장)")
    @ApiResponse(responseCode = "400", description = "존재하지 않은 방 코드 입력")
    @ApiResponse(responseCode = "406", description = "방 최대 인원 초과")
    public ResponseEntity<?> joinRoom(@PathVariable String roomPin, HttpServletResponse response,
                                      @CookieValue(name = "anonymousCode", defaultValue = "") String anonymousCode) {
        // 1. Validation
        try {
            // 2. Business Logic
            Room targetRoom = roomService.findRoomByPin(roomPin);
            RoomDto targetRoomDto = new RoomDto(targetRoom);

            // 3. Make Response
            if (anonymousCode.equals("")) {
                if(targetRoom.getMaxParticipantCount() <= targetRoom.getMaxParticipantCount())
                    return new ResponseEntity<>("최대 인원을 초과했습니다.", HttpStatus.NOT_ACCEPTABLE);
                return new ResponseEntity<>(targetRoomDto, HttpStatus.MOVED_PERMANENTLY);
            } else {
                Participant participant = roomService.findParticipantByUuid(anonymousCode);

                if (!participant.getRoom().getPin().equals(roomPin)) {
                    // 방이 다르다면 쿠키 제거
                    deleteAnonymousCodeCookie(response);
                    return new ResponseEntity<>(targetRoomDto, HttpStatus.MOVED_PERMANENTLY);
                }

                ParticipantDto participantDto = new ParticipantDto(participant);
                return ResponseEntity.ok(participantDto);
            }
        } catch (NullPointerException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // TODO 비적절 이름 필터 넣은 후 관련 예외 추가하여야함 + 테스트도
    @PostMapping("/{roomPin}/signup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path"),
    })
    @Operation(summary = "익명사용자 정보 등록 후 방 입장", description = "닉네임(nickname)과 이름(name) 입력 후 방에 입장합니다.")
    @ApiResponse(responseCode = "201", description = "유저 생성 성공 -> 방 입장, 사용자 정보 포함")
    @ApiResponse(responseCode = "400", description = "이름 혹은 닉네임 불충분 혹은 부적절")
    @ApiResponse(responseCode = "406", description = "이미 존재하는 참가자 정보 혹은 더 이상 참가할 수 없는 방")
    public ResponseEntity<?> signUpParticipant(@PathVariable String roomPin, @RequestBody ParticipantCreateForm participateForm,
                                               HttpServletResponse response) {
        // 1. Validation
//        if(bindingResult.hasErrors()){
//            log.info("errors = {}", bindingResult);
//            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//        }

        try {
            // 2. Business Logic
            Room targetRoom = roomService.findRoomByPin(roomPin);
            Room targetRoom2 = roomService.findRoomByPin(roomPin);

            System.out.println(targetRoom);
            System.out.println(targetRoom2);

            // 3. Make Response
            Cookie anonymousCookie = Room.setAnonymousCookie();
            response.addCookie(anonymousCookie);

            Participant savedParticipant = roomService.joinParticipant(participateForm, targetRoom, UUID.fromString(anonymousCookie.getValue()).toString());
            ParticipantDto participantDto = new ParticipantDto(savedParticipant);
            return ResponseEntity.status(HttpStatus.CREATED).body(participantDto);
        }catch(NullPointerException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(IllegalAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // TODO Pageable 적용해야함.. 왠지 모르겠는데 오류남
    // TODO 쿼리 효율이 좋지 않다. 방을 조회하고, 유저를 조회하여서.. 유저로만 조회할 수 있도록 uuid에 방pin을 붙여도 좋아보인다.
    @GetMapping("/{roomPin}/participants")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @Operation(summary = "방 참여자 목록 조회", description = "해당 방에 참여한 참여자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "참여자 목록 조회 성공!")
    @ApiResponse(responseCode = "400", description = "존재하지 않는 방 코드 입력")
    public ResponseEntity<List<ParticipantDto>> printParticipants(@PathVariable String roomPin){
        try {
            Room targetRoom = roomService.findRoomByPin(roomPin);
        } catch(InvalidRoomAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(roomService.findParticipantsByRoomPin(roomPin));
    }

    private void deleteAnonymousCodeCookie(HttpServletResponse response) {
        Cookie anonymousCookie = new Cookie("anonymousCode", null);
        anonymousCookie.setMaxAge(0); // 지연시간 제거
        anonymousCookie.setPath("/"); // 모든 경로에서 삭제
        response.addCookie(anonymousCookie);
    }
}