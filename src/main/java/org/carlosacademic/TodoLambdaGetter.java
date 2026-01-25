package org.carlosacademic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.carlosacademic.model.CreateTodo;
import org.carlosacademic.model.TodoDTO;
import org.carlosacademic.proxy.TodoProxy;

public class TodoLambdaGetter implements RequestHandler<CreateTodo, TodoDTO> {

    private final TodoProxy proxy;

    public TodoLambdaGetter() {
        this.proxy = new TodoProxy();
    }

    @Override
    public TodoDTO handleRequest(CreateTodo input, Context context) {
        return proxy.getTodo(input.id(), context);
    }
}
