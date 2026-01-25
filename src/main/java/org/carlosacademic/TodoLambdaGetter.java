package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.proxy.TodoProxy;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, String> {

    private final TodoProxy proxy;

    public TodoLambdaGetter() {
        this.proxy = new TodoProxy();
    }

    @Override
    public String handleRequest(CreateTodo input, Context context) {
        return proxy.getTodo(input.id(), context);
    }
}
