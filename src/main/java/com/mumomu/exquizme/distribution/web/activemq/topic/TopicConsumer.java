package com.mumomu.exquizme.distribution.web.activemq.topic;

import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@EnableJms
@RequiredArgsConstructor
public class TopicConsumer {
    // 테스트용
    @JmsListener(destination = "${spring.activemq.topic.name}", containerFactory = "jsaFactory")
    public void appleReceive(@Payload AnswerSubmitForm answer,
                             @Headers Map<String, Object> headers,
                             MessageHeaders messageHeaders,
                             JmsMessageHeaderAccessor messageHeaderAccessor) {
        System.out.println(answer.getSessionId());
        System.out.println(answer.getProblemIdx());
        System.out.println(answer.getAnswerText());

        System.out.println(headers);
        System.out.println(messageHeaders);

        System.out.println(" [ContentType]:" + messageHeaderAccessor.getContentType());
        System.out.println(" [CorrelationId]:" + messageHeaderAccessor.getCorrelationId());
        System.out.println(" [DeliveryMode]:" + messageHeaderAccessor.getDeliveryMode());
        System.out.println(" [Destination]:" + messageHeaderAccessor.getDestination());
        System.out.println(" [Priority]" + messageHeaderAccessor.getPriority());
    }
}