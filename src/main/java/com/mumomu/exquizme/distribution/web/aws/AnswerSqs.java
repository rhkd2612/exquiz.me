package com.mumomu.exquizme.distribution.web.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.InvalidMessageContentsException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.mumomu.exquizme.distribution.domain.Answer;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

public class AnswerSqs {
    @Value("${cloud.aws.sqs.queue-name}")
    private static String QUEUE_NAME;
    private AmazonSQS sqs;
    public static final String SQS_RESULT_SUCCESS = "SUCCESS";
    public static final String SQS_RESULT_FAILED = "FAILED";

    AnswerSqs(){
        this.sqs = AmazonSQSClientBuilder.defaultClient();
    }

    public String sendAnswer(Answer answer) throws Exception{
        try{
            // create queue
            sqs.createQueue(QUEUE_NAME);

            // get queue url
            String queryUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

            // create request
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(queryUrl)
                    .withMessageBody(answer.toString())
                    .withDelaySeconds(5);

            // send
            SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);

            // get response
            if(Objects.isNull(sendMessageResult.getMessageId()) || "".equals(sendMessageResult.getMessageId())){
                throw new NullPointerException(SQS_RESULT_FAILED);
            }

            return SQS_RESULT_SUCCESS;
        }catch (UnsupportedOperationException | InvalidMessageContentsException | NullPointerException e) {
            throw new Exception("error on sending one Order");
        }
    }
}
