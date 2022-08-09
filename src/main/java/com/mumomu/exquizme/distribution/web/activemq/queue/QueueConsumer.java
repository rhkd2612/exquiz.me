package com.mumomu.exquizme.distribution.web.activemq.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room/consume")
@RequiredArgsConstructor
public class QueueConsumer {
//    private final JmsTemplate jmsTemplate;
//    private final Queue queue;
//
//    @GetMapping("/message")
//    public AnswerSubmitForm consumeMessage(){
//        AnswerSubmitForm answer = null;
//        try{
//            ObjectMapper mapper = new ObjectMapper();
//            String jsonMessage = (String) jmsTemplate.receiveAndConvert(queue);
//            answer = mapper.readValue(jsonMessage, AnswerSubmitForm.class);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//        return answer;
//    }
}
