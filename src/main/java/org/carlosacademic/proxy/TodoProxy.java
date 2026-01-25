package org.carlosacademic.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoProxy {

    private static final String API_URL = System.getenv("TODO_API_URL");
    private static final String QUEUE_URL = System.getenv("TODO_QUEUE_URL");
    private final SqsClient sqsClient;

    public TodoProxy() {
        this.sqsClient = SqsClient.create();
    }

    public String getTodo(String id, Context context) {
        LambdaLogger logger = context.getLogger();
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL +"/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 && response.body()!=null){
                logger.log("The todo was obtained successfully");

                SendMessageRequest request = SendMessageRequest.builder()
                        .queueUrl(QUEUE_URL)
                        .messageBody(response.body())
                        .build();

                sqsClient.sendMessage(request);
                return "Todo published to SQS";
            }else{
                return "Failed creating todo whith status code: "+ response.statusCode();
            }
        }catch (Exception e){
            logger.log("Error creating the todo "+ e.getMessage());
        }
        return "Error creating the todo";
    }
}
