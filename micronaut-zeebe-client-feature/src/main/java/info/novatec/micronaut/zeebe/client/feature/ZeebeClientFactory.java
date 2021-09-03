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
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        ZeebeClientBuilder clientBuilder = ZeebeClient.newClientBuilder();

        // TODO: Configuration

        ZeebeClient client = clientBuilder
                .usePlaintext()
                .build();

        log.info("ZeebeClient connected to {} and ready to process tasks", client.getConfiguration().getGatewayAddress());

        return client;
    }

}
