package com.mumomu.exquizme.distribution.controller;

import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.jms.DeliveryMode;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final JmsTemplate jmsTemplate;
    private final ActiveMQTopic tempTopic;

    @MessageMapping("/{roomPin}/mySubmit")
    public void submit(@DestinationVariable String roomPin, @RequestBody AnswerSubmitForm answerSubmitForm) {
        // TODO 방 닫힐 때 topic 제거해주어야함 혹은 비워주기
        System.out.println("asjdnaskldnakjdnklsndaka");

        jmsTemplate.convertAndSend(tempTopic, answerSubmitForm, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });
    }
}
