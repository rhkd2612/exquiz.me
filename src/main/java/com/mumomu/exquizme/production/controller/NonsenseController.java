package com.mumomu.exquizme.production.controller;

import com.mumomu.exquizme.production.dto.NonsenseDto;
import com.mumomu.exquizme.production.service.NonsenseService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = {"넌센스 퀴즈 컨트롤러"})
@RequestMapping("/api")
public class NonsenseController {
    private final NonsenseService nonsenseService;

    @GetMapping("/nonsense")
    @Operation(summary = "넌센스 문제 하나를 랜덤으로 문제 - 답 형식으로 전송")
    @ApiResponse(responseCode = "200", description = "넌센스 문제 전송 성공")
    public ResponseEntity<?> getNonsense() {
        Pair<String, String> pair = nonsenseService.getNonsense();
        return new ResponseEntity<>(new NonsenseDto(pair.getFirst(), pair.getSecond()), HttpStatus.OK);
    }
}
