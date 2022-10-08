package info.novatec.micronaut.zeebe.client.example;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.micronaut.context.ApplicationContext;
import io.zeebe.containers.ZeebeContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Testcontainers
class TestcontainersIntegrationTest {

    @Container
    ZeebeContainer zeebeContainer = new ZeebeContainer(DockerImageName.parse("camunda/zeebe:8.1.0"));

    @Test
    @Timeout(value = 5, unit = TimeUnit.MINUTES)
    void workerShouldProcessWork() throws InterruptedException {

        Logger logger = (Logger) LoggerFactory.getLogger(GoodbyeHandler.class);

        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        try (ApplicationContext applicationContext = ApplicationContext.run(
                Collections.singletonMap("zeebe.client.cloud.gateway-address", zeebeContainer.getExternalGatewayAddress())
        )) {
            while (listAppender.list.stream().noneMatch(e -> e.getFormattedMessage().contains("Retrieved value"))) {
                System.out.println("Waiting another second...");
                Thread.sleep(1000);
            }
        }
    }
}
