package org.carlosacademic.proxy;

import org.carlosacademic.exceptions.ApiException;
import org.carlosacademic.model.CreateTodo;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoProxy {

    private final String API_URL;
    private final HttpClient client;

    public TodoProxy(String API_URL, HttpClient client) {
        this.API_URL = API_URL;
        this.client = client;
    }

    public String getTodo(CreateTodo todo, Logger logger) throws IOException, InterruptedException {
        if (todo == null || todo.id() == null) {
            throw new ApiException(400, "Invalid Todo body");
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL +"/todos/"+todo.id()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200 && response.body()!=null){
            logger.info("The todo was obtained successfully");
            return response.body();
        }else{
            logger.warn("Failed obtaining todo with status code: {}", response.statusCode());
            throw new ApiException(response.statusCode(), "Failed obtaing the todo: " + response.body());
        }
    }
}
