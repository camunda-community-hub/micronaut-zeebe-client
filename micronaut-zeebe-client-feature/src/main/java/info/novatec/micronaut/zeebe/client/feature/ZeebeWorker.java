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

import io.micronaut.context.annotation.Executable;
import io.micronaut.context.annotation.Parallel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.time.Duration;

/**
 * @author Martin Sawilla
 * @author Stefan Schultz
 * @author Stephan Seelig
 * @author Tobias Schäfer
 */
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Documented
@Executable(processOnStartup = true)
@Parallel
public @interface ZeebeWorker {

    /**
     * Set the type of jobs to work on.
     *
     * @see io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1#jobType(String)
     *
     * @return the job type
     */
    String type();

    /**
     * Set the time for how long a job is exclusively assigned for this worker, e.g "PT5M", see format definition in {@link java.time.Duration#parse(CharSequence)}
     *
     * See also {@link io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3#timeout(Duration)}
     *
     * @return the job timeout
     */
    String timeout() default "";

    /**
     * Set the maximum number of jobs which will be exclusively activated for this worker at the same time.
     *
     * See also {@link io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3#maxJobsActive(int)}}
     *
     * @return the maximum number of active jobs
     */
    int maxJobsActive() default -1;

    /**
     * Set the request timeout for activate job request used to poll for new job, e.g "PT20S", see format definition in {@link java.time.Duration#parse(CharSequence)}
     *
     * See also {@link io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3#requestTimeout(Duration)}
     *
     * @return the request timeout
     */
    String requestTimeout() default "";

    /**
     * Set the maximal interval between polling for new jobs, e.g "PT0.1S" for 100ms, see format definition in {@link java.time.Duration#parse(CharSequence)}
     *
     * See also {@link io.camunda.zeebe.client.api.worker.JobWorkerBuilderStep1.JobWorkerBuilderStep3#pollInterval(Duration)}
     *
     * @return poll interval
     */
    String pollInterval() default "";
}
