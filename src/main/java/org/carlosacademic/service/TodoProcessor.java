package org.carlosacademic.service;

import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

public class TodoProcessor {

    private final String QUEUE_URL;
    private final SqsClient sqsClient;

    public TodoProcessor(String QUEUE_URL, SqsClient sqsClient) {
        this.QUEUE_URL = QUEUE_URL;
        this.sqsClient = sqsClient;
    }

    public void processTodo(String todo, Logger logger, String correlationId) {
        logger.info("EVENT=PROCESS_TODO todo={} requestId={}", todo, correlationId);
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(QUEUE_URL)
                .messageBody(todo)
                .messageAttributes(Map.of(
                        "correlationId", MessageAttributeValue.builder()
                                            .stringValue(correlationId)
                                            .dataType("String")
                                            .build()
                ))
                .build();

        sqsClient.sendMessage(request);

        logger.info("EVENT=PROCESS_TODO STATUS_SUCCESS todo={} requestId={}", todo, correlationId);
    }
}
