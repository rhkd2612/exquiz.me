package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.exception.InvalidRoomAccessException;
import com.mumomu.exquizme.distribution.service.AnswerService;
import com.mumomu.exquizme.distribution.service.RoomProgressService;
import com.mumomu.exquizme.distribution.service.RoomService;
import com.mumomu.exquizme.distribution.web.dto.ParticipantDto;
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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"퀴즈방 진행에 이용되는 컨트롤러"})
@RequestMapping("/api/room/{roomPin}")
public class RoomProgressController {
    private final RoomService roomService;
    private final RoomProgressService roomProgressService;
    private final ProblemService problemService;
    private final AnswerService answerService;

    // TODO DTO로 변경해야함
    @GetMapping("/submitList")
    @Operation(summary = "이번문제 정답제출 리스트", description = "방의 현재 문제의 제출 결과를 반환합니다.")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "퀴즈 없음")
    public ResponseEntity<?> submitList(@PathVariable String roomPin){
        try {
            int currentProblemNum = roomService.findRoomByPin(roomPin).getCurrentProblemNum();
            return ResponseEntity.ok(answerService.findAnswerListByProblemIdx(roomPin, currentProblemNum));
        }catch(InvalidRoomAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "리더보드", description = "방의 지금까지 점수 리스트를 반환합니다.(내림차순)")
    @ApiImplicitParam(name = "roomPin", value = "방의 핀번호(Path)", required = true, dataType = "String", paramType = "path")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "퀴즈 없음")
    public ResponseEntity<?> leaderboard(@PathVariable String roomPin){
        try {
            List<ParticipantDto> resultLeaderboard = roomService.findParticipantsByRoomPin(roomPin).stream().map(p -> new ParticipantDto(p)).collect(Collectors.toList());

            Collections.sort(resultLeaderboard,
                    (p1, p2) -> p2.getCurrentScore() - p1.getCurrentScore());

            return ResponseEntity.ok(resultLeaderboard);
        }catch(InvalidRoomAccessException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
