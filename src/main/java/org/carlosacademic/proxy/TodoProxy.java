package org.carlosacademic.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.carlosacademic.model.TodoDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoProxy {

    private static final String API_URL = System.getenv("TODO_API_URL");
    private final ObjectMapper objectMapper;

    public TodoProxy() {
        this.objectMapper = new ObjectMapper();
    }

    public TodoDTO getTodo(String id, Context context) {
        LambdaLogger logger = context.getLogger();
        try (HttpClient client = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL +"/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 && response.body()!=null){
                logger.log("The todo was obtained successfully");
                TodoDTO todoDTO = objectMapper.readValue(response.body(), TodoDTO.class);
                logger.log("Todo parsed successfully");
                return todoDTO;
            }else{
                logger.log("Failed creating todo whith status code: "+ response.statusCode());
                return null;
            }
        }catch (Exception e){
            logger.log("Error creating the todo "+ e.getMessage());
        }
        return null;
    }
}
