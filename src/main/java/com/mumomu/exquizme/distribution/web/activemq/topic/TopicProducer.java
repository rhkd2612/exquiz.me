package com.mumomu.exquizme.distribution.web.activemq.topic;

import com.mumomu.exquizme.distribution.web.model.AnswerSubmitForm;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.jms.DeliveryMode;
import java.util.UUID;

@RestController
@Component
@RequiredArgsConstructor
@ApiIgnore // 현재 미 사용중인 컨트롤러
@RequestMapping("/api/room/topic/producer")
public class TopicProducer {
    private final JmsTemplate jmsTemplate;
    private final ActiveMQTopic answerTopic;

    @PostMapping("/message")
    public AnswerSubmitForm sendMessage(@RequestBody AnswerSubmitForm answer) {
        jmsTemplate.convertAndSend(answerTopic, answer, message -> {
            message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            message.setJMSPriority(10);
            return message;
        });

        return answer;
    }
}
