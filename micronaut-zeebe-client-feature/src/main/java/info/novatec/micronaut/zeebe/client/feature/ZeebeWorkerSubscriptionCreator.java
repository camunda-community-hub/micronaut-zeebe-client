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
import io.camunda.zeebe.client.api.worker.JobHandler;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Martin Sawilla
 * @author Stefan Schultz
 * @author Stephan Seelig
 * @author Tobias Schäfer
 *
 * Allows to configure an external task worker with the {@link ZeebeWorker} annotation. This allows to easily build
 * external workers for multiple topics.
 */
@Context
public class ZeebeWorkerSubscriptionCreator {

    private static final Logger log = LoggerFactory.getLogger(ZeebeWorkerSubscriptionCreator.class);

    protected final BeanContext beanContext;
    protected final ZeebeClient zeebeClient;
    protected Configuration configuration;

    public ZeebeWorkerSubscriptionCreator(BeanContext beanContext,
                                          ZeebeClient zeebeClient,
                                          Configuration configuration) {
        this.beanContext = beanContext;
        this.zeebeClient = zeebeClient;

        this.configuration = configuration;

        beanContext.getBeanDefinitions(JobHandler.class).forEach(this::registerExternalTaskHandler);
    }

    protected void registerExternalTaskHandler(BeanDefinition<JobHandler> beanDefinition) {
        JobHandler externalTaskHandler = beanContext.getBean(beanDefinition);
        AnnotationValue<ZeebeWorker> annotationValue = beanDefinition.getAnnotation(ZeebeWorker.class);

        if (annotationValue != null) {
            String type = annotationValue.stringValue("type").get();

            zeebeClient.newWorker().jobType(type).handler(externalTaskHandler).open();

            log.info("Zeebe client subscribed to type '{}'", type);

        } else {
            log.warn("Skipping subscription. Could not find annotation ExternalTaskSubscription on class {}", beanDefinition.getName());
        }
    }

}
