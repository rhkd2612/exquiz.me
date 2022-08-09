package com.mumomu.exquizme.distribution.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.jms.Queue;

@RestController
@RequestMapping("/api/room/consume")
@RequiredArgsConstructor
public class NewConsumer {
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
