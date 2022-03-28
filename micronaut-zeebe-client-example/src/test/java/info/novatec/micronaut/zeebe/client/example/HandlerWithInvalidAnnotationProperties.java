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
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

/**
 * This handler contains an invalid annotation properties which will make the startup fail.
 *
 * @author Tobias Schäfer
 */
@Singleton
@Requires(property = "handlerWithInvalidAnnotationPropertiesEnabled", value="true")
public class HandlerWithInvalidAnnotationProperties {

    @ZeebeWorker(type = "some-random-type-invalid", timeout = "TwentySeconds")
    public void doSomething(JobClient client, ActivatedJob job) {
        throw new UnsupportedOperationException("");
    }
}
