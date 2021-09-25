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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;

import java.util.Optional;

/**
 * @author Martin Sawilla
 * @author Stefan Schultz
 * @author Stephan Seelig
 * @author Tobias Schäfer
 */
@Context
@ConfigurationProperties("zeebe.client.cloud")
public interface Configuration {

    /**
     * The clusterId when connecting to Camunda Cloud. Don't set this for a local Zeebe Broker.
     *
     * @return the clusterId
     */
    Optional<String> getClusterId();

    /**
     * The clientId to connect to Camunda Cloud. Don't set this for a local Zeebe Broker.
     *
     * @return the the clientId
     */
    Optional<String> getClientId();

    /**
     * The clientSecret to connect to Camunda Cloud. Don't set this for a local Zeebe Broker.
     *
     * @return the name of the clientSecret
     */
    Optional<String> getClientSecret();

}
