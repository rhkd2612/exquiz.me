package com.mumomu.exquizme.distribute.controller;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/swagger/api")
@Api(tags={"TEST - API 정보를 제공하는 컨트롤러"})
public class SwaggerTestController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name="x", value="x 값", required = true, dataType = "int", paramType = "path"),
            @ApiImplicitParam(name="y", value="y 값", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping("/plus/{x}")
    public int plus(@PathVariable int x, @RequestParam int y){
        return x+y;
    }

//    @ApiResponse(code = 502, message = "사용자의 나이가 10살 이하일 때")
//    @ApiOperation(value® = "사용자의 이름과 나이를 리턴하는 메소드")
//    @GetMapping("/user")
//    public UserRes user(UserReq userReq){
//        return new UserRes(userReq.getName(), userReq.getAge());
//    }
//
//    @PostMapping("/user")
//    public UserRes userPost(@RequestBody UserReq req){
//        return new UserRes(req.getName(), req.getAge());
//    }
}
