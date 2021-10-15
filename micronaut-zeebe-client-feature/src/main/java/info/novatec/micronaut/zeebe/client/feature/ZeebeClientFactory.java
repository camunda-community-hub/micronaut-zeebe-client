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
    public ZeebeClient buildClient(Configuration configuration) {

        ZeebeClientBuilder zeebeClientBuilder = isCloudConfigurationPresent(configuration)
                ? createCloudClient(configuration)
                : ZeebeClient.newClientBuilder().usePlaintext();

        configuration.getDefaultRequestTimeout().ifPresent(timeout -> zeebeClientBuilder.defaultRequestTimeout(Duration.parse(timeout)));
        configuration.getDefaultJobPollInterval().ifPresent(duration -> zeebeClientBuilder.defaultJobPollInterval(Duration.ofMillis(duration)));
        configuration.getDefaultJobTimeout().ifPresent(timeout -> zeebeClientBuilder.defaultJobTimeout(Duration.parse(timeout)));
        configuration.getDefaultMessageTimeToLive().ifPresent( ttl -> zeebeClientBuilder.defaultMessageTimeToLive(Duration.parse(ttl)));
        configuration.getDefaultJobWorkerName().ifPresent(zeebeClientBuilder::defaultJobWorkerName);
        configuration.getGatewayAddress().ifPresent(zeebeClientBuilder::gatewayAddress);
        configuration.getNumJobWorkerExecutionThreads().ifPresent(zeebeClientBuilder::numJobWorkerExecutionThreads);
        configuration.getKeepAlive().ifPresent(keepAlive -> zeebeClientBuilder.keepAlive(Duration.parse(keepAlive)));
        configuration.getCaCertificatePath().ifPresent(zeebeClientBuilder::caCertificatePath);

        ZeebeClient zeebeClient = zeebeClientBuilder.build();
        log.info("ZeebeClient is configured to connect to gateway: {}", zeebeClient.getConfiguration().getGatewayAddress());
        return zeebeClient;
    }

    protected ZeebeClientBuilder createCloudClient(Configuration configuration) {
        ZeebeClientCloudBuilderStep4 builder = ZeebeClient.newCloudClientBuilder()
                .withClusterId(configuration.getClusterId().get())
                .withClientId(configuration.getClientId().get())
                .withClientSecret(configuration.getClientSecret().get());
        configuration.getRegion().ifPresent(builder::withRegion);
        return builder;
    }

    protected boolean isCloudConfigurationPresent(Configuration configuration) {
        return configuration.getClusterId().isPresent()
                && configuration.getClientId().isPresent()
                && configuration.getClientSecret().isPresent();
    }

}
