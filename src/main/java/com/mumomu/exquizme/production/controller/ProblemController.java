package com.mumomu.exquizme.production.controller;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.dto.*;
import com.mumomu.exquizme.production.exception.*;
import com.mumomu.exquizme.production.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// TODO 제작파트 JWT 자격 증명 추가해야함
@RestController
@RequiredArgsConstructor
@Api(tags = {"퀴즈 제작 컨트롤러"})
@RequestMapping("/api")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping("/problemset")
    @Operation(summary = "문제 셋 생성", description = "문제 셋을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "문제 셋 생성 성공")
    @ApiResponse(responseCode = "404", description = "호스트 없음")
    public ResponseEntity<?> makeProblemset(@RequestBody ProblemsetSaveDto problemsetSaveDto) {
        try {
            Problemset problemset = problemService.makeProblemset(
                    problemsetSaveDto.getHostId(),
                    problemsetSaveDto.getTitle(),
                    problemsetSaveDto.getDescription(),
                    problemsetSaveDto.getClosingMent());
            return new ResponseEntity<>(new ProblemsetDto(problemset), HttpStatus.CREATED);
        } catch (HostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/problem")
    @Operation(summary = "문제 생성", description = "문제를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "문제 생성 성공")
    @ApiResponse(responseCode = "400", description = "dtype 오류")
    @ApiResponse(responseCode = "404", description = "문제 셋 없음")
    public ResponseEntity<?> makeProblem(@RequestBody ProblemSaveDto problemSaveDto) {
        try {
            Problem problem = problemService.makeProblem(
                    problemSaveDto.getProblemsetId(), problemSaveDto.getDtype(),
                    problemSaveDto.getIdx(), problemSaveDto.getTitle(),
                    problemSaveDto.getDescription(), problemSaveDto.getTimelimit(),
                    problemSaveDto.getScore(), problemSaveDto.getPicture(),
                    problemSaveDto.getAnswer());

            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.CREATED);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/problem_option")
    @Operation(summary = "선택지 생성", description = "선택지를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "선택지 생성 성공")
    @ApiResponse(responseCode = "400", description = "dtype 오류")
    @ApiResponse(responseCode = "404", description = "문제 없음")
    public ResponseEntity<?> makeProblemOption(@RequestBody ProblemOptionSaveDto problemOptionSaveDto) {
        try {
            ProblemOption problemOption = problemService.makeProblemOption(
                    problemOptionSaveDto.getProblemId(),
                    problemOptionSaveDto.getIdx(),
                    problemOptionSaveDto.getDescription(),
                    problemOptionSaveDto.getPicture()
            );
            return new ResponseEntity<>(new ProblemOptionDto(problemOption), HttpStatus.CREATED);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ProblemOptionAccessToSubjectiveProblemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/problemsets/{hostId}")
    @ApiImplicitParam(name = "hostId", value = "호스트 id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problemset 목록 조회", description = "유효한 호스트 id인지 검사 후 problemset 리스트 전송")
    @ApiResponse(responseCode = "200", description = "문제 셋 조회 성공")
    @ApiResponse(responseCode = "404", description = "호스트 없음")
    public ResponseEntity<?> findProblemsets(@PathVariable Long hostId) {
        try {
            List<ProblemsetDto> problemsetDtos = problemService.getProblemsetsByHostId(hostId).stream().map(ps -> new ProblemsetDto(ps)).collect(Collectors.toList());
            return new ResponseEntity<>(problemsetDtos, HttpStatus.OK);
        } catch (HostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/problems/{problemsetId}")
    @ApiImplicitParam(name = "problemsetId", value = "problemset id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem 목록 조회", description = "유효한 problemset id인지 검사 후 problem 리스트 idx순으로 전송")
    @ApiResponse(responseCode = "200", description = "문제 조회 성공")
    @ApiResponse(responseCode = "404", description = "문제 셋 없음")
    public ResponseEntity<?> findProblems(@PathVariable Long problemsetId) {
        try {
            List<ProblemDto> problemDtos = problemService.getProblemsByProblemsetId(problemsetId).stream().map(p -> new ProblemDto(p)).collect(Collectors.toList());
            return new ResponseEntity<>(problemDtos, HttpStatus.OK);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/problem_options/{problemId}")
    @ApiImplicitParam(name = "problemId", value = "problem id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem option 목록 조회", description = "유효한 problem id인지 검사 후 problem option 리스트 idx순으로 전송")
    @ApiResponse(responseCode = "200", description = "선택지 조회 성공")
    @ApiResponse(responseCode = "400", description = "주관식 문제에서 선택지 조회 불가")
    @ApiResponse(responseCode = "404", description = "문제 없음")
    public ResponseEntity<?> findProblemOptions(@PathVariable Long problemId) throws Exception {
        try {
            List<ProblemOptionDto> problemOptionDtos = problemService.getProblemOptionById(problemId).stream().map(po -> new ProblemOptionDto(po)).collect(Collectors.toList());
            return new ResponseEntity<>(problemOptionDtos, HttpStatus.OK);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ProblemOptionAccessToSubjectiveProblemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/problemset")
    @Operation(summary = "문제 셋 수정", description = "문제 셋을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "문제 셋 수정 성공")
    @ApiResponse(responseCode = "404", description = "호스트 없음")
    public ResponseEntity<?> updateProblemset(@RequestBody ProblemsetModifyDto problemsetModifyDto) {
        try {
            Problemset problemset = problemService.updateProblemset(
                    problemsetModifyDto.getProblemsetId(), problemsetModifyDto.getTitle(),
                    problemsetModifyDto.getDescription(), problemsetModifyDto.getClosingMent());

            return new ResponseEntity<>(new ProblemsetDto(problemset), HttpStatus.OK);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/problem")
    @Operation(summary = "문제 수정", description = "문제를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "문제 수정 성공")
    @ApiResponse(responseCode = "400", description = "dtype 오류")
    @ApiResponse(responseCode = "404", description = "문제 셋 없음")
    public ResponseEntity<?> updateProblem(@RequestBody ProblemModifyDto problemModifyDto) throws Exception {
        try {
            Problem problem = problemService.updateProblem(
                    problemModifyDto.getProblemId(), problemModifyDto.getDtype(),
                    problemModifyDto.getIdx(), problemModifyDto.getTitle(),
                    problemModifyDto.getDescription(), problemModifyDto.getTimelimit(),
                    problemModifyDto.getScore(), problemModifyDto.getPicture(),
                    problemModifyDto.getAnswer());

            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.OK);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DifferentDtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/problem_option")
    @Operation(summary = "선택지 수정", description = "선택지를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "선택지 수정 성공")
    @ApiResponse(responseCode = "404", description = "문제 없음")
    public ResponseEntity<?> updateProblemOption(@RequestBody ProblemOptionModifyDto problemOptionModifyDto) throws Exception {
        try {
            ProblemOption problemOption = problemService.updateProblemOption(
                    problemOptionModifyDto.getProblemOptionId(), problemOptionModifyDto.getIdx(),
                    problemOptionModifyDto.getDescription(), problemOptionModifyDto.getPicture());

            return new ResponseEntity<>(new ProblemOptionDto(problemOption), HttpStatus.OK);
        } catch (ProblemOptionNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/problemset/{problemsetId}")
    @Operation(summary = "문제 셋 삭제", description = "문제 셋을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "문제 셋 삭제 성공")
    @ApiResponse(responseCode = "404", description = "문제 셋 없음")
    public ResponseEntity<?> deleteProblemset(@PathVariable Long problemsetId) {
        try {
            problemService.deleteProblemset(problemsetId);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/problem/{problemId}")
    @Operation(summary = "문제 삭제", description = "문제를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "문제 삭제 성공")
    @ApiResponse(responseCode = "404", description = "문제 없음")
    public ResponseEntity<?> deleteProblem(@PathVariable Long problemId) {
        try {
            problemService.deleteProblem(problemId);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

}
