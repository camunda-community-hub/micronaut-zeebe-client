package info.novatec.micronaut.zeebe.client.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.Process;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Singleton
public class ProcessDeployer {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDeployer.class);
    private final ZeebeClient zeebeClient;

    public ProcessDeployer(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @EventListener
    @Async
    public void onStartupEvent(StartupEvent event) {
        DeploymentEvent deployment = zeebeClient.newDeployCommand()
                .addResourceFromClasspath("bpmn/say_hello.bpmn")
                .requestTimeout(Duration.ofSeconds(10))
                .send()
                .join();
        Process process = deployment.getProcesses().get(0);
        logger.info("deployed process {} with id {}", process.getResourceName(), process.getProcessDefinitionKey());
    }

}
