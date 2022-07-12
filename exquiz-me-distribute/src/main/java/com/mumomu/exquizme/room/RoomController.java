package com.mumomu.exquizme.room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomController {
    // 퀴즈방 입장
    @GetMapping("/room/{roomId}")
    public String participateRoom(@PathVariable String roomId, Model model){
        model.addAttribute("roomId", roomId);
        return "room";
    }

    // 퀴즈방 생성
//    @PostMapping("/room")
//    @ResponseBody
//    public RoomDto createRoom(@ModelAttribute UserDto user) {
//
//    }
}
