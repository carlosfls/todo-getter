package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.proxy.TodoProxy;
import org.carlosacademic.service.TodoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.http.HttpClient;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, String> {

    private static final Logger logger = LoggerFactory.getLogger(TodoLambdaGetter.class);

    private static final String API_URL = System.getenv("TODO_API_URL");
    private static final String QUEUE_URL = System.getenv("TODO_QUEUE_URL");
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final SqsClient sqsClient = SqsClient.create();

    private final TodoProxy proxy;
    private final TodoProcessor processor;

    public TodoLambdaGetter() {
        this.proxy = new TodoProxy(API_URL, client);
        this.processor = new TodoProcessor(QUEUE_URL, sqsClient);
    }

    @Override
    public String handleRequest(CreateTodo input, Context context) {
        String todo = proxy.getTodo(input.id(), logger);

        context.getLogger().log("Request id: " + context.getAwsRequestId() + "");

        return processor.processTodo(todo, logger);
    }
}
