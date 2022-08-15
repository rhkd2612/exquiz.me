package com.mumomu.exquizme.distribution.web.aws;

import com.amazonaws.services.sns.AmazonSNS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AwsSnsClient {
    private final AmazonSNS amazonSNS;

    public void publish(String topicArn, String message){
        amazonSNS.publish(topicArn, message);
    }
}
