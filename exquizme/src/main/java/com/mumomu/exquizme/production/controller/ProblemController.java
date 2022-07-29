package com.mumomu.exquizme.production.controller;

import com.mumomu.exquizme.production.domain.Problem;
import com.mumomu.exquizme.production.domain.ProblemOption;
import com.mumomu.exquizme.production.domain.Problemset;
import com.mumomu.exquizme.production.dto.*;
import com.mumomu.exquizme.production.service.ProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = {"퀴즈 제작 컨트롤러"})
@RequestMapping("/api")
public class ProblemController {
    private final ProblemService problemService;

    @PostMapping("/problemset")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostId", value = "problemset이 추가될 host id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "problemset 제목", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "problemset 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "closingMent", value = "클로징 멘트", required = true, dataType = "String", paramType = "query"),
    })
    public Problemset makeProblemset(@RequestBody ProblemsetSaveDto problemsetSaveDto) throws Exception {
        Problemset problemset = problemService.makeProblemset(
                problemsetSaveDto.getHostId(),
                problemsetSaveDto.getTitle(),
                problemsetSaveDto.getDescription(),
                problemsetSaveDto.getClosingMent());

        return problemset;
    }

    @PostMapping("/problem")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemsetId", value = "문제가 추가될 problemset의 id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "dtype", value = "문제 유형", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "index", value = "문제 번호", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "title", value = "문제 제목", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "문제 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "timelimit", value = "문제 시간 제한", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "score", value = "문제 점수", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "picture", value = "첨부 이미지", required = false, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "answer", value = "답을 String으로 변환한 값", required = true, dataType = "String", paramType = "query")
    })
    public Problem makeProblem(@RequestBody ProblemSaveDto problemSaveDto) throws Exception {
        Problem problem = problemService.makeProblem(
                problemSaveDto.getProblemsetId(), problemSaveDto.getDtype(),
                problemSaveDto.getIndex(), problemSaveDto.getTitle(),
                problemSaveDto.getDescription(), problemSaveDto.getTimelimit(),
                problemSaveDto.getScore(), problemSaveDto.getPicture(),
                problemSaveDto.getAnswer());

        return problem;
    }

    @PostMapping("/problem_option")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problem", value = "problem option이 추가될 problem id", required = true, dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "index", value = "problem option 번호", required = true, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "description", value = "problem option 설명", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "picture", value = "problem option 사진", required = false, dataType = "String", paramType = "query"),
    })
    public ProblemOption makeProblemOption(@RequestBody ProblemOptionSaveDto problemOptionSaveDto) throws Exception {
        ProblemOption problemOption = problemService.makeProblemOption(
                problemOptionSaveDto.getProblemId(),
                problemOptionSaveDto.getIndex(),
                problemOptionSaveDto.getDescription(),
                problemOptionSaveDto.getPicture()
        );
        return problemOption;
    }

    @GetMapping("/problemsets/{hostId}")
    @ApiImplicitParam(name = "hostId", value = "호스트 id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problemset 목록 조회", description = "유효한 호스트 id인지 검사 후 problemset 리스트 전송")
    public List<Problemset> findProblemsets(@PathVariable Long hostId) {
        List<Problemset> problemsets = problemService.getProblemset(hostId);
        return problemsets;
    }

    @GetMapping("/problems/{problemsetId}")
    @ApiImplicitParam(name = "problemsetId", value = "problemset id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem 목록 조회", description = "유효한 problemset id인지 검사 후 problem 리스트 index순으로 전송")
    public List<Problem> findProblems(@PathVariable Long problemsetId) {
        List<Problem> problems = problemService.getProblem(problemsetId);
        return problems;
    }

    @GetMapping("/problem_options/{problemId}")
    @ApiImplicitParam(name = "problemId", value = "problem id", required = true, dataType = "Long", paramType = "path")
    @Operation(summary = "호스트가 가지고 있는 problem option 목록 조회", description = "유효한 problem id인지 검사 후 problem option 리스트 index순으로 전송")
    public List<ProblemOption> findProblemOptions(@PathVariable Long problemId) throws Exception {
        List<ProblemOption> problemOptions = problemService.getProblemOption(problemId);
        return problemOptions;
    }

    @PutMapping("/problemset")
    public Problemset updateProblemset(@RequestBody ProblemsetModifyDto problemsetModifyDto) throws Exception {
        Problemset problemset = problemService.updateProblemset(
                problemsetModifyDto.getProblemsetId(), problemsetModifyDto.getTitle(),
                problemsetModifyDto.getDescription(), problemsetModifyDto.getClosingMent());

        return problemset;
    }

    @PutMapping("/problem")
    public Problem updateProblem(@RequestBody ProblemModifyDto problemModifyDto) throws Exception {
        Problem problem = problemService.updateProblem(
                problemModifyDto.getProblemId(), problemModifyDto.getDtype(),
                problemModifyDto.getIndex(), problemModifyDto.getTitle(),
                problemModifyDto.getDescription(), problemModifyDto.getTimelimit(),
                problemModifyDto.getScore(), problemModifyDto.getPicture(),
                problemModifyDto.getAnswer());

        return problem;
    }

    @PutMapping("/problem_option")
    public ProblemOption updateProblemOption(@RequestBody ProblemOptionModifyDto problemOptionModifyDto) throws Exception {
        ProblemOption problemOption = problemService.updateProblemOption(
                problemOptionModifyDto.getProblemOptionId(), problemOptionModifyDto.getIndex(),
                problemOptionModifyDto.getDescription(), problemOptionModifyDto.getPicture());

        return problemOption;
    }

}
