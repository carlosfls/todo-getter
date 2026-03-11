package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.exceptions.ApiException;
import org.carlosacademic.model.ApiResponseDto;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.proxy.TodoProxy;
import org.carlosacademic.service.TodoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.http.HttpClient;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, ApiResponseDto> {

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
    public ApiResponseDto handleRequest(CreateTodo input, Context context) {
        try {
            String todo = proxy.getTodo(input, logger);

            logger.info("Request id: {} ",context.getAwsRequestId());

            processor.processTodo(todo, logger);

            return new ApiResponseDto(200, "Todo Sent successfully");
        }catch (ApiException e){
            logger.error("Business error: {}",e.getMessage());
           return getApiResponseError(e);
        }catch (Exception e) {
            logger.error("Unexpected error: {}",e.getMessage());
            return new ApiResponseDto(500, "Internal Server Error");
        }
    }

    private ApiResponseDto getApiResponseError(ApiException e){
        return new ApiResponseDto(e.getStatusCode(), e.getMessage());
    }
}
