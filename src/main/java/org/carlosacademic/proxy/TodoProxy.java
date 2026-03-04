package org.carlosacademic.proxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

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

    public String getTodo(String id, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL +"/todos/"+id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 && response.body()!=null){
                logger.log("The todo was obtained successfully");
                return response.body();
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
