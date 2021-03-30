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

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.inject.BeanDefinition;
import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.topic.TopicSubscriptionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Martin Sawilla
 *
 * Allows to configure an external task worker with the {@link ExternalTaskSubscription} annotation. This allows to easily build
 * external workers for multiple topics.
 */
@Context
public class ExternalWorkerSubscriptionCreator {

    private static final Logger log = LoggerFactory.getLogger(ExternalWorkerSubscriptionCreator.class);

    protected final BeanContext beanContext;
    protected final ExternalTaskClient externalTaskClient;
    protected Configuration configuration;

    public ExternalWorkerSubscriptionCreator(BeanContext beanContext,
                                             ExternalTaskClient externalTaskClient,
                                             Configuration configuration) {
        this.beanContext = beanContext;
        this.externalTaskClient = externalTaskClient;

        this.configuration = configuration;

        beanContext.getBeanDefinitions(ExternalTaskHandler.class).forEach(this::registerExternalTaskHandler);
    }

    protected void registerExternalTaskHandler(BeanDefinition<ExternalTaskHandler> beanDefinition) {
        ExternalTaskHandler externalTaskHandler = beanContext.getBean(beanDefinition);
        AnnotationValue<ExternalTaskSubscription> annotationValue = beanDefinition.getAnnotation(ExternalTaskSubscription.class);

        if (annotationValue != null) {
            //noinspection OptionalGetWithoutIsPresent
            String topicName = annotationValue.stringValue("topicName").get();

            TopicSubscriptionBuilder builder = createTopicSubscription(externalTaskHandler, externalTaskClient, topicName, annotationValue);

            Map<String, Configuration.Subscription> subscriptions = configuration.getSubscriptions();
            if (subscriptions != null && subscriptions.containsKey(topicName)) {
                Configuration.Subscription subscription = subscriptions.get(topicName);
                if (subscription != null) {
                    overrideTopicSubscriptionWithConfigurationProperties(subscription, builder, topicName);
                }
            }

            builder.open();
            log.info("External task client subscribed to topic '{}'", topicName);

        } else {
            log.warn("Skipping subscription. Could not find annotation ExternalTaskSubscription on class {}", beanDefinition.getName());
        }
    }

    protected TopicSubscriptionBuilder createTopicSubscription(ExternalTaskHandler externalTaskHandler, ExternalTaskClient client, String topicName, AnnotationValue<ExternalTaskSubscription> annotationValue) {

        TopicSubscriptionBuilder builder = client.subscribe(topicName);

        builder.handler(externalTaskHandler);

        annotationValue.longValue("lockDuration").ifPresent(builder::lockDuration);

        annotationValue.get("variables", String[].class).ifPresent(it -> {
            if (!it[0].equals("")) {
                builder.variables(it);
            }
        });

        annotationValue.booleanValue("localVariables").ifPresent(builder::localVariables);

        annotationValue.stringValue("businessKey").ifPresent(builder::businessKey);

        annotationValue.stringValue("processDefinitionId").ifPresent(builder::processDefinitionId);

        annotationValue.get("processDefinitionIdIn", String[].class).ifPresent(it -> {
            if (!it[0].equals("")) {
                builder.processDefinitionIdIn(it);
            }
        });

        annotationValue.stringValue("processDefinitionKey").ifPresent(builder::processDefinitionKey);

        annotationValue.get("processDefinitionKeyIn", String[].class).ifPresent(it -> {
            if (!it[0].equals("")) {
                builder.processDefinitionKeyIn(it);
            }
        });

        annotationValue.stringValue("processDefinitionVersionTag").ifPresent(builder::processDefinitionVersionTag);

        annotationValue.booleanValue("withoutTenantId").ifPresent(it -> {
            if (it) {
                builder.withoutTenantId();
            }
        });

        annotationValue.get("tenantIdIn", String[].class).ifPresent(it -> {
            if (!it[0].equals("")) {
                builder.tenantIdIn(it);
            }
        });

        annotationValue.booleanValue("includeExtensionProperties").ifPresent(builder::includeExtensionProperties);

        return builder;
    }

    protected void overrideTopicSubscriptionWithConfigurationProperties(Configuration.Subscription subscription, TopicSubscriptionBuilder builder, String topicName) {
        log.info("External configuration for topic {} found.", topicName);

        if (subscription.getLockDuration() != null) {
            builder.lockDuration(subscription.getLockDuration());
        }

        if (subscription.getVariables() != null) {
            builder.variables(subscription.getVariables());
        }

        if (subscription.getLocalVariables() != null) {
            builder.localVariables(subscription.getLocalVariables());
        }

        if (subscription.getBusinessKey() != null) {
            builder.businessKey((subscription.getBusinessKey()));
        }

        if (subscription.getProcessDefinitionId() != null) {
            builder.processDefinitionId(subscription.getProcessDefinitionId());
        }

        if (subscription.getProcessDefinitionIdIn() != null) {
            builder.processDefinitionIdIn(subscription.getProcessDefinitionIdIn());
        }

        if (subscription.getProcessDefinitionKey() != null) {
            builder.processDefinitionKey(subscription.getProcessDefinitionKey());
        }

        if (subscription.getProcessDefinitionKeyIn() != null) {
            builder.processDefinitionKeyIn(subscription.getProcessDefinitionKeyIn());
        }

        if (subscription.getProcessDefinitionVersionTag() != null) {
            builder.processDefinitionVersionTag(subscription.getProcessDefinitionVersionTag());
        }

        if (subscription.getWithoutTenantId() != null && subscription.getWithoutTenantId()) {
            builder.withoutTenantId();
        }

        if (subscription.getTenantIdIn() != null) {
            builder.tenantIdIn(subscription.getTenantIdIn());
        }

        if (subscription.getIncludeExtensionProperties() != null) {
            builder.includeExtensionProperties(subscription.getIncludeExtensionProperties());
        }
    }
}
