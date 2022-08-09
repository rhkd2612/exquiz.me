package com.mumomu.exquizme.distribution.web.activemq.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/produce")
@RequiredArgsConstructor
public class QueueProducer {
//    private final JmsTemplate jmsTemplate;
//    private final Queue queue;
//
//    @PostMapping("/message")
//    public AnswerSubmitForm sendMessage(@RequestBody AnswerSubmitForm answer){
//        try{
//            ObjectMapper mapper = new ObjectMapper();
//            String answerAsJson = mapper.writeValueAsString(answer);
//            jmsTemplate.convertAndSend(queue, answerAsJson);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//        return answer;
//    }
}
