package com.mumomu.exquizme.production.crawling.controller;

import com.mumomu.exquizme.production.crawling.service.S3Uploader;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController @RequiredArgsConstructor
@Api(tags = {"S3 업로드 컨트롤러"})
@RequestMapping("/api")
public class S3Controller {
    private final S3Uploader s3Uploader;

    @PostMapping("/image/upload/file")
    @Operation(summary = "파일 시스템 이미지 업로드", description = "MultipartFile를 입력받아 이미지를 S3에 업로드하고 S3 url를 반환")
    @ApiResponse(responseCode = "200", description = "업로드 성공")
    @ApiResponse(responseCode = "400", description = "업로드 실패")
    public ResponseEntity<?> uploadByFileSystem(@RequestParam("file") MultipartFile multipartFile) {
        try {
            return new ResponseEntity<>(s3Uploader.upload(multipartFile, "static"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/image/upload")
    @Operation(summary = "이미지 url로 이미지 업로드", description = "url을 입력받아 이미지를 S3에 업로드하고 S3 url를 반환")
    @ApiResponse(responseCode = "200", description = "업로드 성공")
    @ApiResponse(responseCode = "400", description = "업로드 실패")
    public ResponseEntity<?> uploadByUrl(@RequestParam("url") String url) {
        try {
            return new ResponseEntity<>(s3Uploader.upload(url, "static"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}