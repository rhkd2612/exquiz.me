package com.mumomu.exquizme.distribution.web.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestListener {
    @SqsListener(value = "ArticleCreate", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void listen(String value, Acknowledgment ack){
        log.info(value);
        ack.acknowledge();
    }
}
