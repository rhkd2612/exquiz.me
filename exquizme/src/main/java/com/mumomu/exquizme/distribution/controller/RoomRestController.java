package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.domain.Participant;
import com.mumomu.exquizme.distribution.domain.Room;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
import com.mumomu.exquizme.distribution.web.dto.RoomDto;
import com.mumomu.exquizme.distribution.web.model.ParticipantForm;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 참여에 이용되는 컨트롤러"})
public class RoomRestController {
    private final RoomService roomService;

    // 퀴즈방 생성(임시)
    @GetMapping("/room/newRoom")
    public ResponseEntity<?> newRoom(){
        Room room = roomService.newRoom();
        RoomDto createRoomDto = new RoomDto(room);
        return ResponseEntity.ok(createRoomDto);
    }

    // 퀴즈방 입장
    // TODO 임시로 defaultValue를 넣어둠 -> 나중에 지워야함
    // TODO Cookie에 관한 수정이 필요함 + WebSocket 사용 시 쿠키가 필요없어질수도..?
    // TODO 다른 방 코드에 들어갔을 경우 존재하는 방인지에 대한 인증 + 기존에 있던 쿠키 삭제 필요
    // TODO Swagger변경 + API 테스트 필요
    @GetMapping("/room/{roomPin}")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "long", paramType = "path")
    @Operation(summary = "퀴즈방 조회", description = "쿠키가 있는지 확인 후 존재 시 방 입장, 미 존재 시 등록 화면으로 이동합니다.")
    @ApiResponse(responseCode = "200", description = "방 입장 성공(기존 쿠키 정보를 토대로 입장)")
    @ApiResponse(responseCode = "400", description = "존재하지 않은 방 코드 입력")
    @ApiResponse(responseCode = "401", description = "방 입장 실패(사용자 정보 입력 필요 -> 사용자 이름/닉네임 등록 씬으로 입장)")
    public ResponseEntity<?> joinRoom(@PathVariable long roomPin, Model model, HttpServletResponse response,
                                      @CookieValue(name = "anonymousCode", defaultValue = "") String anonymousCode) {
        Room targetRoom = roomService.findRoomByPin(roomPin);
        // 1. Validation
        // 현재는 방 등록이 없으므로 주석
        if (targetRoom == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        // 2. Business Logic
        RoomDto targetRoomDto = new RoomDto(targetRoom);

        // 3. Make Response
        if (anonymousCode.equals("")) {
            return new ResponseEntity<>(targetRoomDto, HttpStatus.UNAUTHORIZED);
        } else {
            Participant participant = roomService.findParticipant(anonymousCode);

            if (participant.getRoom().getPin() != roomPin) {
                // 방이 다르다면 쿠키 제거
                deleteAnonymousCodeCookie(response);
                return new ResponseEntity<>(targetRoomDto, HttpStatus.UNAUTHORIZED);
            }

            ParticipantDto participantDto = new ParticipantDto(participant);
            return ResponseEntity.ok(participantDto);
        }
    }


    //
//    @GetMapping("/room/{roomId}/participate")
//    public ResponseEntity<?>

    @PostMapping("/room/{roomPin}/signup")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "long", paramType = "path"),
            @ApiImplicitParam(name = "nickname", value = "익명사용자 닉네임", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "익명사용자 이름", required = true, dataType = "String", paramType = "query"),
    })
    @Operation(summary = "익명사용자 정보 등록 후 방 입장", description = "닉네임(nickname)과 이름(name) 입력 후 방에 입장합니다.")
    @ApiResponse(responseCode = "201", description = "유저 생성 성공 -> 방 입장, 사용자 정보 포함")
    @ApiResponse(responseCode = "400", description = "이름 혹은 닉네임 불충분 혹은 부적절")
    public ResponseEntity<?> signUpParticipant(@PathVariable long roomPin, @RequestBody @Valid ParticipantForm participateForm,
                                               BindingResult bindingResult, HttpServletResponse response) {
        log.info("create new Participant");
        // 1. Validation
        if(bindingResult.hasErrors()){
            log.info("errors = {}", bindingResult);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        // 2. Business Logic

        // 3. Make Response
        Cookie anonymousCookie = Room.setAnonymousCookie();
        response.addCookie(anonymousCookie);

        Participant participant = Participant.builder().name(participateForm.getName()).nickname(participateForm.getNickname()).build();
        participant.setUuid(UUID.fromString(anonymousCookie.getValue()).toString());

        ParticipantDto participantDto = new ParticipantDto(participant);

        return ResponseEntity.status(HttpStatus.CREATED).body(participantDto);
    }


    // 퀴즈방 생성
//    @PostMapping("/room")
//    @ResponseBody
//    public RoomDto createRoom(@ModelAttribute UserDto user) {
//
//    }

    private void deleteAnonymousCodeCookie(HttpServletResponse response) {
        Cookie anonymousCookie = new Cookie("anonymousCode", null);
        anonymousCookie.setMaxAge(0); // 지연시간 제거
        anonymousCookie.setPath("/"); // 모든 경로에서 삭제
        response.addCookie(anonymousCookie);
    }
}
