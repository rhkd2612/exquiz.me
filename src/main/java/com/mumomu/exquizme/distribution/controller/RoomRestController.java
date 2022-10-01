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

    // TODO 상태코드 전부 변경해야함 API 맞춰보기전
    /*
    * HTTP 상태코드
    종류
    1XX (Infomational) : 요청이 수신되어 처리중
    거의 사용되지 않음
    2XX (Successful) : 요청 정상 처리
    200 OK
    201 Created
    요청이 성공해서 새로운 리소스가 생성됨
    202 Accepted
    요청이 접수되었으나 처리가 완료되지 않았음
    배치 처리 같은 곳에서 사용
    요청 접수 후 1시간 뒤에 배치 프로세스가 요청을 처리
    204 No Content
    서버가 요청을 성공적으로 수행했지만, 응답 페이로드 본문에 보낼 데이터가 없음
    save 버튼
    save 버튼의 결과로 아무 내용이 없어도 된다
    save 버튼을 눌러도 같은 화면을 유지해야 한다
    결과 내용이 없어도 204 메시지만으로 성공을 인식할 수 있다
    3XX (Redirection) : 요청이 완료되려면 추가 행동이 필요
    리다이렉션이란? 응답의 결과에 Location 헤더가 있으면, Location 위치로 자동 이동
    종류
    영구 리다이렉션 - 특정 리소스의 URI가 영구적으로 이동
    리소스의 URI가 영구적으로 이동했음을 알려준다
    301
    리다이렉트 시 요청 메서드가 GET으로 변하고, 본문이 제거될 수 있음
    308
    리다이렉트시 요청 메서드와 본문 유지(처음 POST 시, 리다이렉트도 POST)
    일시 리다이렉션 - 일시적인 변경
    주문 완료 후 주문 내역 화면으로 이동
    302 Found → GET으로 변할 수 있음
    리다이렉트시 요청 메서드가 GET으로 변하고, 본문이 제거될 수 있음
    307 Temporary Redirect → Method가 변하면 안됨
    302와 기능은 같음
    리다이렉트시 요청 메서드와 본문 유지(요청 메서드를 변경하면 안된다)
    303 See Other → 메서드가 GET으로 변경
    302와 기능은 같음
    리다이렉트 시 요청 메서드가 GET으로 변경
    PRG(Post Redirect Get)
    POST로 주문 후에 웹 브라우저를 새로고침하면?
    새로 고침은 다시 요청되서 다시 재주문이 될 수 있다
    POST로 주문 후에 새로 고침으로 인한 중복 주문 방지
    POST로 주문 후에 주문 겨로가 화면을 GET 메서드로 리다이렉트
    새로고침해도 결과 화면을 GET으로 조회
    중복 주문 대신에 결과 화면만 GET으로 다시 요청
    특수 리다이렉션
    결과 대신 캐시를 사용
    300 Multiple Choices : 안 쓴다
    304 Not Modified
    캐시를 목적으로 사용
    클라이언트에게 리소스가 수정되지 않았음을 알려줌. 따라서 클라이언트는 로컬 PC에 저장된 캐시를 재사용(캐시로 리다이렉트)
    304 응답은 응답에 메시지 바디를 포함하면 안된다 (로컬 캐시 사용으로)
    조건부 GET, HEAD 요청 시 사용
    4XX (Client Error) : 클라이언트 오류, 잘못된 문법 등으로 서버가 요청을 수행할 수 없음
    똑같은 재시도가 실패를 계속한다 (400대는 복구 불가, 500대와의 차이)
    요청 구문, 메시지 등등 오류
    401 Unauthorized
    클라이언트가 해당 리소스에 대한 인증이 필요
    인증 : 본인이 누군인지 확인(로그인)
    인가 : 권한부여(Admin 권한처럼 특정 리소스에 접근할 수 있는 권한, 인증이 있어야 인가가 있음)
    403 Forbidden
    서버가 요청을 이해했지만 승인을 거부함
    주로 인증 자격 증명이 있지만, 접근 권한이 불충분한 경우
    404 Not Found
    요청 리소스가 서버에 없음
    또는 클라이언트가 권한이 부족한 리소스에 접근할 때 해당 리소스를 숨기고 싶을 때
    5XX (Server Error) : 서버 오류, 서버가 정상 요청을 처리하지 못함
    서버에 문제가 있기 때문에 재시도 시 성공할 수도 있음
    500 Internal Server Error
    서버 내부 문제로 오류 발생
    503 Service Unavailable
    서비스 이용 불가
    서버가 일시적인 과부하 또는 예정된 작업으로 잠시 요청을 처리할 수 없음
    Retry-After 헤더 필드로 얼마뒤에 복귀되는지 보낼 수도 있음
    만약 모르는 상태코드가 나타나면?
    위의 상위 상태코드로 처리
    * */

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
            Room room = roomService.newRoom(problemset, roomCreateForm.getMaxParticipantCount());

            RoomDto createRoomDto = new RoomDto(room);
            // 3. Make Response
            return new ResponseEntity(createRoomDto, HttpStatus.CREATED);
        } catch (CreateRandomPinFailureException e) {
            log.info("error : " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.REQUEST_TIMEOUT);
        } catch (NullPointerException e) {
            log.info(e.getMessage());
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
            log.info(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalAccessException e) {
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            List<Participant> targetParticipants = roomService.findParticipantsByRoomPin(roomPin);
            return ResponseEntity.ok(targetParticipants.stream().map(ParticipantDto::new).collect(Collectors.toList()));
        } catch (InvalidRoomAccessException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}