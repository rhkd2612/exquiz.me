package com.mumomu.exquizme.distribution.web.activemq.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.jms.Queue;

@RestController
@RequestMapping("/api/room/queue/producer")
@ApiIgnore // 현재 미 사용중인 컨트롤러
@RequiredArgsConstructor
public class QueueProducer {
    private final JmsTemplate jmsTemplate;
    private final Queue queue;

    @PostMapping("/message")
    public AnswerSubmitForm sendMessage(@RequestBody AnswerSubmitForm answer){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String answerAsJson = mapper.writeValueAsString(answer);
            jmsTemplate.convertAndSend(queue, answerAsJson);
        } catch(Exception e){
            e.printStackTrace();
        }

        return answer;
    }
}
