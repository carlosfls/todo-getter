package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.exceptions.ApiException;
import org.carlosacademic.model.ApiResponseDto;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.proxy.TodoProxy;
import org.carlosacademic.service.TodoProcessor;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.http.HttpClient;
import java.time.Duration;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, ApiResponseDto> {

    private static final String API_URL = System.getenv("TODO_API_URL");
    private static final String QUEUE_URL = System.getenv("TODO_QUEUE_URL");

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private static final SqsClient sqsClient = SqsClient.create();

    private final TodoProxy proxy;
    private final TodoProcessor processor;

    public TodoLambdaGetter() {
        this.proxy = new TodoProxy(API_URL, client);
        this.processor = new TodoProcessor(QUEUE_URL, sqsClient);
    }

    @Override
    public ApiResponseDto handleRequest(CreateTodo input, Context context) {
        String correlationId = context.getAwsRequestId();
        LambdaLogger logger = context.getLogger();

        try {
            logger.log("Getting TODO with id: "+ input.id() + " Request id: " + correlationId);
            String todo = proxy.getTodo(input, logger, correlationId);

            logger.log("Processing TODO with id: "+ input.id() + " Request id: " + correlationId);
            processor.processTodo(todo, logger, correlationId);

            return new ApiResponseDto(200, "Todo Sent successfully");
        }catch (ApiException e){
            logger.log("Business error: "+e.getMessage()+" Request id: "+ correlationId);
           return getApiResponseError(e);
        }catch (Exception e) {
            logger.log("Unexpected error: "+e.getMessage()+" Request id: "+ correlationId);
            return new ApiResponseDto(500, "Internal Server Error");
        }
    }

    private ApiResponseDto getApiResponseError(ApiException e){
        return new ApiResponseDto(e.getStatusCode(), e.getMessage());
    }
}
