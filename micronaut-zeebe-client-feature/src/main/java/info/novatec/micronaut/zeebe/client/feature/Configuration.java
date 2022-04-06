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

import io.camunda.zeebe.client.ZeebeClientBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;

import java.time.Duration;
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
     * The clusterId when connecting to Camunda Platform 8. Don't set this for a local Zeebe Broker.
     * @see io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1#withClusterId(String) 
     *
     * @return the clusterId
     */
    Optional<String> getClusterId();

    /**
     * The clientId to connect to Camunda Platform 8. Don't set this for a local Zeebe Broker.
     * @see io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1.ZeebeClientCloudBuilderStep2#withClientId(String) 
     *
     * @return the the clientId
     */
    Optional<String> getClientId();

    /**
     * The clientSecret to connect to Camunda Platform 8. Don't set this for a local Zeebe Broker.
     * @see io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1.ZeebeClientCloudBuilderStep2.ZeebeClientCloudBuilderStep3#withClientSecret(String) 
     *
     * @return the name of the clientSecret
     */
    Optional<String> getClientSecret();

    /**
     * The region of the Camunda Platform 8 cluster
     * @see io.camunda.zeebe.client.ZeebeClientCloudBuilderStep1.ZeebeClientCloudBuilderStep2.ZeebeClientCloudBuilderStep3.ZeebeClientCloudBuilderStep4#withRegion(String) 
     *
     * @return the region where your cluster is located
     */
    Optional<String> getRegion();

    /**
     * Whether to connect with plain text or SSL/TLS. This option is not evaluated when connecting to Camunda Platform 8 which always uses a secure connection via SSL/TLS.
     * @see ZeebeClientBuilder#usePlaintext()
     *
     * @return whether the connection is using plain text or SSL/TLS
     */
    Optional<Boolean> getUsePlainTextConnection();

    /**
     * the default request timeout as ISO 8601 standard formatted String
     * e.g. PT20S for a timeout of 20 seconds
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#defaultRequestTimeout(Duration) 
     *
     * @return the default request timeout
     */
    Optional<String> getDefaultRequestTimeout();

    /**
     * the default job poll interval in milliseconds
     * e.g. 100 for a timeout of 100 milliseconds
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#defaultJobPollInterval(Duration) 
     *
     * @return the default job poll interval
     */
    Optional<Long> getDefaultJobPollInterval();

    /**
     * the default job timeout as ISO 8601 standard formatted String
     * e.g. PT5M for a timeout of 5 minutes
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#defaultJobTimeout(Duration) 
     *
     * @return the default job timeout
     */
    Optional<String> getDefaultJobTimeout();

    /**
     * the default message time to live as ISO 8601 standard formatted String
     * e.g. PT1H for a timeout of 1 hour
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#defaultMessageTimeToLive(Duration) 
     *
     * @return the default message time to live
     */
    Optional<String> getDefaultMessageTimeToLive();

    /**
     * the default job worker name
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#defaultJobWorkerName(String) 
     *
     * @return the default name of a worker
     */
    Optional<String> getDefaultJobWorkerName();

    /**
     * the gateway address to which the client should connect
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#gatewayAddress(String) 
     *
     * @return the gateway address
     */
    Optional<String> getGatewayAddress();

    /**
     * the number of threads used to execute workers
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#numJobWorkerExecutionThreads(int) 
     *
     * @return the count of job worker execution threads
     */
    Optional<Integer> getNumJobWorkerExecutionThreads();

    /**
     * the interval for keep allive messages to be sent as ISO 8601 standard formatted String
     * e.g. PT45S for 45 seconds
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#keepAlive(Duration) 
     *
     * @return the interval to send keep alive message
     */
    Optional<String> getKeepAlive();

    /**
     * the path to a ca certificate
     * @see io.camunda.zeebe.client.ZeebeClientBuilder#caCertificatePath(String) 
     *
     * @return the custom ca certificate path
     */
    Optional<String> getCaCertificatePath();
}
