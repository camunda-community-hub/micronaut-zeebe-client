# micronaut-camunda-server-application

This example shows how to apply the external task pattern: the server with a BPMN process will create tasks which will be executed by an external task client.

Start Server:

`./gradlew run -p micronaut-camunda-server-example`

This will output something like:

```
Created number: 83
Created number: 34
Created number: 55
Created number: 14
```

Now start the worker to process the external tasks, see [micronaut-camunda-external-client-example](/micronaut-camunda-external-client-example).
