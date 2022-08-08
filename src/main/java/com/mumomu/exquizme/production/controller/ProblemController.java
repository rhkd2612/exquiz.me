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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Api(tags = {"퀴즈 제작 컨트롤러"})
@RequestMapping("/api")
public class ProblemController {
    private final ProblemService problemService;

    //TODO RepsonseEntity + Dto로 변경
    @PostMapping("/problemset")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostId", value = "problemset이 추가될 host id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "problemset 제목", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "problemset 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "closingMent", value = "클로징 멘트", required = true, dataType = "String", paramType = "query"),
    })
    public ResponseEntity<?> makeProblemset(@RequestBody ProblemsetSaveDto problemsetSaveDto) {
        try {
            Problemset problemset = problemService.makeProblemset(
                    problemsetSaveDto.getHostId(),
                    problemsetSaveDto.getTitle(),
                    problemsetSaveDto.getDescription(),
                    problemsetSaveDto.getClosingMent());
            return new ResponseEntity<>(new ProblemsetDto(problemset), HttpStatus.OK);
        } catch (HostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/problem")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemsetId", value = "문제가 추가될 problemset의 id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "dtype", value = "문제 유형", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "idx", value = "문제 번호", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "문제 제목", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "문제 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "timelimit", value = "문제 시간 제한", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "score", value = "문제 점수", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "picture", value = "첨부 이미지", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "answer", value = "답을 String으로 변환한 값", required = true, dataType = "String", paramType = "query")
    })
    public ResponseEntity<?> makeProblem(@RequestBody ProblemSaveDto problemSaveDto) {
        try {
            Problem problem = problemService.makeProblem(
                    problemSaveDto.getProblemsetId(), problemSaveDto.getDtype(),
                    problemSaveDto.getIdx(), problemSaveDto.getTitle(),
                    problemSaveDto.getDescription(), problemSaveDto.getTimelimit(),
                    problemSaveDto.getScore(), problemSaveDto.getPicture(),
                    problemSaveDto.getAnswer());

            return new ResponseEntity<>(new ProblemDto(problem), HttpStatus.OK);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/problem_option")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problem", value = "problem option이 추가될 problem id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "idx", value = "problem option 번호", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "problem option 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "picture", value = "problem option 사진", required = false, dataType = "String", paramType = "query"),
    })
    public ResponseEntity<?> makeProblemOption(@RequestBody ProblemOptionSaveDto problemOptionSaveDto) {
        try {
            ProblemOption problemOption = problemService.makeProblemOption(
                    problemOptionSaveDto.getProblemId(),
                    problemOptionSaveDto.getIdx(),
                    problemOptionSaveDto.getDescription(),
                    problemOptionSaveDto.getPicture()
            );
            return new ResponseEntity<>(new ProblemOptionDto(problemOption), HttpStatus.OK);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> findProblemsets(@PathVariable Long hostId) {
        try {
            List<ProblemsetDto> problemsetDtos = problemService.getProblemsetsByHostId(hostId).stream().map(ps -> new ProblemsetDto(ps)).collect(Collectors.toList());
            return new ResponseEntity<>(problemsetDtos, HttpStatus.OK);
        } catch (HostNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/problems/{problemsetId}")
    @ApiImplicitParam(name = "problemsetId", value = "problemset id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem 목록 조회", description = "유효한 problemset id인지 검사 후 problem 리스트 idx순으로 전송")
    public ResponseEntity<?> findProblems(@PathVariable Long problemsetId) {
        try {
            List<ProblemDto> problemDtos = problemService.getProblemsByProblemsetId(problemsetId).stream().map(p -> new ProblemDto(p)).collect(Collectors.toList());
            return new ResponseEntity<>(problemDtos, HttpStatus.OK);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/problem_options/{problemId}")
    @ApiImplicitParam(name = "problemId", value = "problem id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem option 목록 조회", description = "유효한 problem id인지 검사 후 problem option 리스트 idx순으로 전송")
    public ResponseEntity<?> findProblemOptions(@PathVariable Long problemId) throws Exception {
        try {
            List<ProblemOptionDto> problemOptionDtos = problemService.getProblemOptionById(problemId).stream().map(po -> new ProblemOptionDto(po)).collect(Collectors.toList());
            return new ResponseEntity<>(problemOptionDtos, HttpStatus.OK);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ProblemOptionAccessToSubjectiveProblemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/problemset")
    public ResponseEntity<?> updateProblemset(@RequestBody ProblemsetModifyDto problemsetModifyDto) {
        try {
            Problemset problemset = problemService.updateProblemset(
                    problemsetModifyDto.getProblemsetId(), problemsetModifyDto.getTitle(),
                    problemsetModifyDto.getDescription(), problemsetModifyDto.getClosingMent());

            return new ResponseEntity<>(new ProblemsetDto(problemset), HttpStatus.OK);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/problem")
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DifferentDtypeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/problem_option")
    public ResponseEntity<?> updateProblemOption(@RequestBody ProblemOptionModifyDto problemOptionModifyDto) throws Exception {
        try {
            ProblemOption problemOption = problemService.updateProblemOption(
                    problemOptionModifyDto.getProblemOptionId(), problemOptionModifyDto.getIdx(),
                    problemOptionModifyDto.getDescription(), problemOptionModifyDto.getPicture());

            return new ResponseEntity<>(new ProblemOptionDto(problemOption), HttpStatus.OK);
        } catch (ProblemOptionNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/problemset/{problemsetId}")
    public ResponseEntity<?> deleteProblemset(@PathVariable Long problemsetId) {
        try {
            problemService.deleteProblemset(problemsetId);
        } catch (ProblemsetNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/problem/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long problemId) {
        try {
            problemService.deleteProblem(problemId);
        } catch (ProblemNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
