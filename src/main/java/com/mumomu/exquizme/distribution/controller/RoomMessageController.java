package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.web.aws.AwsSnsClient;
import com.mumomu.exquizme.distribution.web.model.QuizMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomMessageController {
    private final SimpMessageSendingOperations sendingOperations;
    private final AwsSnsClient awsSnsClient;

    @Value("${cloud.aws.sns.arns.create-article}")
    private String createArticleArn;

    @PostMapping("/mypub")
    public void pub(){
        awsSnsClient.publish(createArticleArn, "test");
    }

    /*
        /sub/room/100000 - 구독(roomPin : 100000)
        /pub/hello - 메시지 발행
     */
    @MessageMapping("/message")
    public void enter(QuizMessage message) {
//        if (message == null)
//            message = new QuizMessage();

//        log.info("MessageController: enter; message={}", message.toString());
//
//        // 2. Business Logic
//        // (1) 메시지 형식에 따라 대화를 생성한다.
//        if (QuizMessage.MessageType.ENTER.equals(message.getType())) {
//            // DB에 대화방 입장 처리는 여기에 추가 필요
//            message.setMessage(String.format("%s 님이 입장하였습니다.", message.getSender()));
//
//        } else if (QuizMessage.MessageType.OUT.equals(message.getType())) {
//            // DB에 대화방 퇴장 처리는 여기에 추가 필요
//            message.setMessage(String.format("%s 님이 나갔습니다.", message.getSender()));
//
//        } else if (QuizMessage.MessageType.WHISPER.equals(message.getType())) {
//            // 특정 사용자에게만 메시지를 전달할 때 처리 (예: 귓속말)
//            // message.setMessage(String.format("%s 님이 귓속말로 전달합니다.<br />", message.getSender(), message.getMessage()));
//
//            String destination = "/topic/chat/room/" + message.getRoomPin();
//            log.info("send ws; destination={}, payload={}", destination, message.toString());

        // (2) 대화를 DB에 저장해야 한다면, 별도의 Thread를 생성하여 처리한다.
        /*
        new Thread(() -> {

            // 여기에서 DB에 저장하는 코드를 추가한다.

        }).start();
        */

        // 3. Make Response
        String destination = "/sub/room/" + message.getRoomPin();
        sendingOperations.convertAndSend(destination, message);
    }
}
