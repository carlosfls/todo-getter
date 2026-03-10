package org.carlosacademic.service;

import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class TodoProcessor {

    private final String QUEUE_URL;
    private final SqsClient sqsClient;

    public TodoProcessor(String QUEUE_URL, SqsClient sqsClient) {
        this.QUEUE_URL = QUEUE_URL;
        this.sqsClient = sqsClient;
    }

    public String processTodo(String todo, Logger logger) {
        if (todo !=null){
            logger.info("Sending to sqs");
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
