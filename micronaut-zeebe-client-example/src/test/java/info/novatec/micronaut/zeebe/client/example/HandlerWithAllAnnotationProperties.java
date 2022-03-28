/*
 * Copyright 2022 original authors
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
package info.novatec.micronaut.zeebe.client.example;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;

/**
 * This handler contains all possible annotation properties. It will never be called but the values will be parsed on startup.
 *
 * @author Tobias Schäfer
 */
@Singleton
public class HandlerWithAllAnnotationProperties {

    @ZeebeWorker(type = "some-random-type", timeout = "PT1S", requestTimeout = "PT2S", pollInterval = "PT3S", maxJobsActive = 99)
    public void doSomething(JobClient client, ActivatedJob job) {
        throw new UnsupportedOperationException("");
    }
}
