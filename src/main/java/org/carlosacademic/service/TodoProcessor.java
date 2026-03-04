package org.carlosacademic.service;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class TodoProcessor {

    private final String QUEUE_URL;
    private final SqsClient sqsClient;

    public TodoProcessor(String QUEUE_URL, SqsClient sqsClient) {
        this.QUEUE_URL = QUEUE_URL;
        this.sqsClient = sqsClient;
    }

    public String processTodo(String todo, Context context) {
        LambdaLogger logger = context.getLogger();
        if (todo !=null){
            logger.log("Sending to sqs");
            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(QUEUE_URL)
                    .messageBody(todo)
                    .build();
            sqsClient.sendMessage(request);
            return "Todo published to SQS";
        }
        return "Error creating the todo";
    }
}
