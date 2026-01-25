package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.proxy.TodoProxy;
import org.carlosacademic.service.TodoProcessor;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, String> {

    private final TodoProxy proxy;
    private final TodoProcessor processor;

    public TodoLambdaGetter() {
        this.proxy = new TodoProxy();
        this.processor = new TodoProcessor();
    }

    @Override
    public String handleRequest(CreateTodo input, Context context) {
        String todo = proxy.getTodo(input.id(), context);
        return processor.processTodo(todo, context);
    }
}
