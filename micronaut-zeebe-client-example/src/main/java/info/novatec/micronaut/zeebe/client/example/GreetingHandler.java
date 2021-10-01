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
package info.novatec.micronaut.zeebe.client.example;

import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stefan Schultz
 * @author Stephan Seelig
 * @author Tobias Schäfer
 *
 * This is an example handler on how to build an JobHandler by annotating the class with {@link ZeebeWorker}.
 *
 * See also {@link GoodbyeHandler}.
 */
@Singleton
public class GreetingHandler {

    private static final Logger log = LoggerFactory.getLogger(GreetingHandler.class);

    @ZeebeWorker(type = "say-hello")
    public void doSomething(JobClient client, ActivatedJob job) {
        log.info("Hello world, from job {}", job.getKey());
        client.newCompleteCommand(job.getKey())
                .variables(String.format("{\"x\": %d}", (int)(Math.random()*100)))
                .send()
                .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
    }
}
