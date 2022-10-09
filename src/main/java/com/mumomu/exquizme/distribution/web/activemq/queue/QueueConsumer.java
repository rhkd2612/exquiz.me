package com.mumomu.exquizme.distribution.web.activemq.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.jms.Queue;

@RestController
@ApiIgnore // 현재 미 사용인 컨트롤러
@RequestMapping("/api/room/queue/consumer")
@RequiredArgsConstructor
public class QueueConsumer {
    private final JmsTemplate jmsTemplate;
    private final Queue queue;

    @GetMapping("/message")
    public AnswerSubmitForm consumeMessage(){
        AnswerSubmitForm answer = null;
        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = (String) jmsTemplate.receiveAndConvert(queue);
            answer = mapper.readValue(jsonMessage, AnswerSubmitForm.class);
        } catch(Exception e){
            e.printStackTrace();
        }

        return answer;
    }
}
