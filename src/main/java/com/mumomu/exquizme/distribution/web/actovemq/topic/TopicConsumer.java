package com.mumomu.exquizme.distribution.web.actovemq.topic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import java.util.Map;

@Component
@EnableJms
@RequiredArgsConstructor
public class TopicConsumer {
    @JmsListener(destination = "${cloud.aws.activemq.topic-name}", containerFactory = "jsaFactory")
    public void appleReceive(@Payload AnswerSubmitForm answer,
                             @Headers Map<String, Object> headers,
                             MessageHeaders messageHeaders,
                             JmsMessageHeaderAccessor messageHeaderAccessor) {
        System.out.println("Receive Answer 1");
        System.out.println(answer.getUuid());
        System.out.println(answer.getProblemIdx());
        System.out.println(answer.getAnswerText());

        System.out.println(" [ContentType]:" + messageHeaderAccessor.getContentType());
        System.out.println(" [CorrelationId]:" + messageHeaderAccessor.getCorrelationId());
        System.out.println(" [DeliveryMode]:" + messageHeaderAccessor.getDeliveryMode());
        System.out.println(" [Destination]:" + messageHeaderAccessor.getDestination());
        System.out.println(" [Priority]" + messageHeaderAccessor.getPriority());
    }
}