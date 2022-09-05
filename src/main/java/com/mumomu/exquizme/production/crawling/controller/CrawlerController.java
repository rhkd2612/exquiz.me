package com.mumomu.exquizme.production.crawling.controller;


import com.mumomu.exquizme.production.crawling.exception.ResultNotFoundException;
import com.mumomu.exquizme.production.crawling.service.CrawlerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequiredArgsConstructor
@Api(tags = {"이미지 크롤링 컨트롤러"})
@RequestMapping("/api")
public class CrawlerController {
    private final CrawlerService crawlerService;

    @GetMapping("/crawl/{keyword}")
    @ApiImplicitParam(name = "keyword", value = "검색어", required = true, dataType = "String", paramType = "path")
    @Operation(summary = "검색어를 기반으로 사진 링크 전송", description = "링크는 List 형식으로 전송됨")
    @ApiResponse(responseCode = "200", description = "사진 리스트 전송 성공")
    @ApiResponse(responseCode = "400", description = "요청 쿼리 잘못됨")
    @ApiResponse(responseCode = "404", description = "찾은 사진 없음")
    public ResponseEntity<?> getImages(@PathVariable String keyword) {
        try {
            List<String> result = crawlerService.crawlImages(keyword);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ResultNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
