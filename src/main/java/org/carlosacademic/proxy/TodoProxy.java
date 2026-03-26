package org.carlosacademic.proxy;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.carlosacademic.exceptions.ApiException;
import org.carlosacademic.model.CreateTodo;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TodoProxy {

    private final String API_URL;
    private final HttpClient client;

    public TodoProxy(String API_URL, HttpClient client) {
        this.API_URL = API_URL;
        this.client = client;
    }

    public String getTodo(CreateTodo todo, LambdaLogger logger, String correlationId) throws IOException, InterruptedException {
        if (todo == null || todo.id() == null) {
            throw new ApiException(400, "Invalid Todo body");
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(5))
                .uri(URI.create(API_URL +"/todos/"+todo.id()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200 && response.body()!=null){
            return response.body();
        }else{
            logger.log("Failed to get the TODO with id: "+ todo.id() + "Request id: " + correlationId);
            throw new ApiException(response.statusCode(), "Failed obtaing the todo: " + response.body());
        }
    }
}
