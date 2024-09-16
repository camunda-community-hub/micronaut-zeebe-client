package info.novatec.micronaut.zeebe.client.example;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.InternalClientException;
import io.micronaut.context.ApplicationContext;
import io.zeebe.containers.ZeebeContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Map;

/**
 * This test verifies that the serialization of {@link java.time.LocalDate} works without any problems by using
 * Micronauts Jackson {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * @author Silvio Wangler
 */
@Testcontainers
public class JacksonSerializationTest {

    private static final String ZEEBE_VERSION = System.getenv("ZEEBE_VERSION");

    @Container
    ZeebeContainer zeebeContainer = new ZeebeContainer(DockerImageName.parse("camunda/zeebe:%s".formatted(ZEEBE_VERSION)));

    @Test
    public void verifyThatSerialisationWorks() {


        // then no exceptions thrown...

        try (ApplicationContext applicationContext = ApplicationContext.run(
                Map.of(
                        "zeebe.client.cloud.gateway-address", zeebeContainer.getExternalGatewayAddress(),
                        "zeebe.client.cloud.use-jackson-mapper-of-micronaut", "true"
                )
        )) {
            applicationContext.getBean(ZeebeClient.class).newCreateInstanceCommand().bpmnProcessId("Process_SayHello").latestVersion().variable("date", LocalDate.now()).send().join();
        }

    }

    @Test
    public void proofThatSerialisationDoesNotWork() {

        try (ApplicationContext applicationContext = ApplicationContext.run(
                Map.of(
                        "zeebe.client.cloud.gateway-address", zeebeContainer.getExternalGatewayAddress(),
                        "zeebe.client.cloud.use-jackson-mapper-of-micronaut", "false"
                )
        )) {
            applicationContext.getBean(ZeebeClient.class).newCreateInstanceCommand().bpmnProcessId("Process_SayHello").latestVersion().variable("date", LocalDate.now()).send().join();
            Assertions.fail("Should have thrown an exception due to LocalDate serialization issue");
        } catch (InternalClientException e) {
            Assertions.assertTrue(e.getMessage().startsWith("Failed to serialize object"));
        }
    }
}
