/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.novatec.micronaut.zeebe.client.feature;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1.ZeebeClientCloudBuilderStep2.ZeebeClientCloudBuilderStep3.ZeebeClientCloudBuilderStep4;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @author Martin Sawilla
 * @author Stefan Schultz
 * @author Stephan Seelig
 * @author Tobias Schäfer
 */
@Factory
public class ZeebeClientFactory {

    private static final Logger log = LoggerFactory.getLogger(ZeebeClientFactory.class);

    @Singleton
    ZeebeClient buildClient(Configuration configuration) {

        ZeebeClientBuilder zeebeClientBuilder;
        if (isCloudConfigurationPresent(configuration)) {
            zeebeClientBuilder = createCloudClient(configuration);
        } else {
            zeebeClientBuilder = ZeebeClient.newClientBuilder().usePlaintext();
        }
        if (configuration.getDefaultRequestTimeout().isPresent()) {
            zeebeClientBuilder.defaultRequestTimeout(Duration.parse(configuration.getDefaultRequestTimeout().get()));
        }
        if (configuration.getDefaultJobPollInterval().isPresent()) {
            zeebeClientBuilder.defaultJobPollInterval(Duration.ofMillis(configuration.getDefaultJobPollInterval().get()));
        }
        if (configuration.getDefaultJobTimeout().isPresent()) {
            zeebeClientBuilder.defaultJobTimeout(Duration.parse(configuration.getDefaultJobTimeout().get()));
        }
        if (configuration.getDefaultMessageTimeToLive().isPresent()) {
            zeebeClientBuilder.defaultMessageTimeToLive(Duration.parse(configuration.getDefaultMessageTimeToLive().get()));
        }
        if (configuration.getDefaultJobWorkerName().isPresent()) {
            zeebeClientBuilder.defaultJobWorkerName(configuration.getDefaultJobWorkerName().get());
        }
        if (configuration.getGatewayAddress().isPresent()) {
            zeebeClientBuilder.gatewayAddress(configuration.getGatewayAddress().get());
        }
        if (configuration.getNumJobWorkerExecutionThreads().isPresent() && configuration.getNumJobWorkerExecutionThreads().get() > 0) {
            zeebeClientBuilder.numJobWorkerExecutionThreads(configuration.getNumJobWorkerExecutionThreads().get());
        }
        if (configuration.getKeepAlive().isPresent()) {
            zeebeClientBuilder.keepAlive(Duration.parse(configuration.getKeepAlive().get()));
        }
        if (configuration.getCaCertificatePath().isPresent()) {
            zeebeClientBuilder.caCertificatePath(configuration.getCaCertificatePath().get());
        }

        ZeebeClient zeebeClient = zeebeClientBuilder.build();
        log.info("ZeebeClient is configured to connect to gateway: {}", zeebeClient.getConfiguration().getGatewayAddress());
        return zeebeClient;
    }

    private ZeebeClientBuilder createCloudClient(Configuration configuration) {
        ZeebeClientCloudBuilderStep4 builder = ZeebeClient.newCloudClientBuilder()
                .withClusterId(configuration.getClusterId().get())
                .withClientId(configuration.getClientId().get())
                .withClientSecret(configuration.getClientSecret().get());
        if (configuration.getRegion().isPresent()) {
            builder.withRegion(configuration.getRegion().get());
        }
        return builder;
    }

    private boolean isCloudConfigurationPresent(Configuration configuration) {
        return configuration.getClusterId().isPresent()
                && configuration.getClientId().isPresent()
                && configuration.getClientSecret().isPresent();
    }

}
