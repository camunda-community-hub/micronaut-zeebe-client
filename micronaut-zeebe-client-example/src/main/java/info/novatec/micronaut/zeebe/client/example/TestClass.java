package info.novatec.micronaut.zeebe.client.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.micronaut.context.annotation.Context;

@Context
public class TestClass {

    private final ZeebeClient zeebeClient;

    public TestClass(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }
}
