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
package info.novatec.micronaut.zeebe.client.feature;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.micronaut.context.BeanContext;
import io.micronaut.context.processor.ExecutableMethodProcessor;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.type.Argument;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tobias Schäfer
 */
@Singleton
public class ZeebeWorkerProcessor implements ExecutableMethodProcessor<ZeebeWorker> {

    private static final Logger log = LoggerFactory.getLogger(ZeebeWorkerProcessor.class);

    protected final BeanContext beanContext;
    protected final ZeebeClient zeebeClient;

    public ZeebeWorkerProcessor(BeanContext beanContext, ZeebeClient zeebeClient) {
        this.beanContext = beanContext;
        this.zeebeClient = zeebeClient;
    }

    @Override
    public void process(BeanDefinition<?> beanDefinition, ExecutableMethod<?, ?> method) {
        registerJobHandler(method);
    }

    protected void registerJobHandler(ExecutableMethod<?, ?> method) {
        AnnotationValue<ZeebeWorker> annotation = method.getAnnotation(ZeebeWorker.class);
        if (methodSignatureMatchesJobHandler(method.getArguments())) {
            Class<?> declaringType = method.getDeclaringType();
            Object bean = beanContext.getBean(declaringType);
            if (annotation != null) {
                annotation.stringValue("type").ifPresent(type -> {
                    zeebeClient
                            .newWorker()
                            .jobType(type)
                            .handler((client, job) -> ((ExecutableMethod) method).invoke(bean, client, job)).open();
                    log.info("Zeebe client ({}#{}) subscribed to type '{}'", bean.getClass().getName(), method.getName(), type);
                });
            }
        }
    }

    protected boolean methodSignatureMatchesJobHandler(Argument<?>[] arguments) {
        return arguments.length == 2 && arguments[0].isAssignableFrom(JobClient.class) && arguments[1].isAssignableFrom(ActivatedJob.class);
    }
}
