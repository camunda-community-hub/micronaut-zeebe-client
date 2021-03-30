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
package info.novatec.micronaut.camunda.external.client.feature;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.ExternalTaskClientBuilder;
import org.camunda.bpm.client.impl.ExternalTaskClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Sawilla
 */
@Factory
public class ExternalTaskClientFactory {

    private static final Logger log = LoggerFactory.getLogger(ExternalTaskClientFactory.class);

    @Singleton
    ExternalTaskClient buildClient(Configuration configuration, ExternalClientCustomizer externalClientCustomizer) {

        ExternalTaskClientBuilder clientBuilder = ExternalTaskClient.create();

        clientBuilder.baseUrl(configuration.getBaseUrl());
        configuration.getWorkerId().ifPresent(clientBuilder::workerId);
        configuration.getMaxTasks().ifPresent(clientBuilder::maxTasks);
        configuration.getUsePriority().ifPresent(clientBuilder::usePriority);
        configuration.getDefaultSerializationFormat().ifPresent(clientBuilder::defaultSerializationFormat);
        configuration.getDateFormat().ifPresent(clientBuilder::dateFormat);
        configuration.getAsyncResponseTimeout().ifPresent(clientBuilder::asyncResponseTimeout);
        configuration.getLockDuration().ifPresent(clientBuilder::lockDuration);
        configuration.getDisableAutoFetching().ifPresent(it -> {
            if(it) {
                clientBuilder.disableAutoFetching();
            }
        });
        configuration.getDisableBackoffStrategy().ifPresent(it -> {
            if(it) {
                clientBuilder.disableBackoffStrategy();
            }
        });

        externalClientCustomizer.customize(clientBuilder);

        ExternalTaskClient client = clientBuilder.build();

        String baseUrl = ((ExternalTaskClientImpl) client).getTopicSubscriptionManager().getEngineClient().getBaseUrl();
        log.info("ExternalTaskClient connected to {} and ready to process tasks", baseUrl);

        return client;
    }
}
