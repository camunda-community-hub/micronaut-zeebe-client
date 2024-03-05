package info.novatec.micronaut.zeebe.client.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@MicronautTest
@Property(name = "zeebe.client.cloud.use-jackson-mapper-of-micronaut", value = "true")
public class JacksonObjectMapperActiveTest {

    @Inject
    ZeebeClient zeebeClient;

    @Test
    public void verifyThatObjectMapperAreTheSame() {

        zeebeClient.newCreateInstanceCommand().bpmnProcessId("Process_SayHello").latestVersion().variable("date", LocalDate.now()).send().join();

        // then no exceptions thrown...
    }
}
