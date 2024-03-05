package info.novatec.micronaut.zeebe.client.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.InternalClientException;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@MicronautTest
@Property(name = "zeebe.client.cloud.use-jackson-mapper-of-micronaut", value = "false")
public class JacksonObjectMapperFalseTest {

    @Inject
    ZeebeClient zeebeClient;

    @Test
    public void verifyThatObjectMapperAreTheSame() {

        try {
            zeebeClient.newCreateInstanceCommand().bpmnProcessId("Process_SayHello").latestVersion().variable("date", LocalDate.now()).send().join();
            Assertions.fail("Should have thrown an exception due to LocalDate serialization issue");
        } catch (InternalClientException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Failed to serialize object"));
        }
    }
}
