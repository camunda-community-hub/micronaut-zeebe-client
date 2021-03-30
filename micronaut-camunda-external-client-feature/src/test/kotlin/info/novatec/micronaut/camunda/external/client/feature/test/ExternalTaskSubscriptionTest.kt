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
package info.novatec.micronaut.camunda.external.client.feature.test

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.camunda.bpm.client.ExternalTaskClient
import org.camunda.bpm.client.impl.ExternalTaskClientImpl
import org.camunda.bpm.client.topic.TopicSubscription
import org.junit.jupiter.api.Test

/**
 * @author Martin Sawilla
 */
@MicronautTest
class ExternalTaskSubscriptionTest {

    @Inject
    lateinit var externalTaskClient: ExternalTaskClient

    @Test
    fun `topic subscription with annotation`() {
        val topicName = "test-topic-annotation"
        val subscription = this.getSubscription(topicName)

        assertThat(subscription.topicName).isEqualTo(topicName)
        assertThat(subscription.lockDuration).isEqualTo(19000)
        assertThat(subscription.variableNames).containsExactly("test-one", "test-two")
        assertThat(subscription.isLocalVariables).isTrue
    }

    @Test
    fun `topic subscription with another annotation`() {
        val topicName = "test-topic-configuration"
        val subscription = this.getSubscription(topicName)

        assertThat(subscription.topicName).isEqualTo(topicName)
    }

    private fun getSubscription(topicName: String): TopicSubscription {
        val client = externalTaskClient as ExternalTaskClientImpl
        val subscriptions = client.topicSubscriptionManager.subscriptions
        return subscriptions.find { it.topicName == topicName }!!
    }
}
