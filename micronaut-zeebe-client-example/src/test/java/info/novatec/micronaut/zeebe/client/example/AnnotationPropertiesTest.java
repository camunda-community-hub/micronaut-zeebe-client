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

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Tobias Schäfer
 */
class AnnotationPropertiesTest {

    @Test
    void testValidAnnotation() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("handlerWithInvalidAnnotationPropertiesEnabled", false);
        try (EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties)) {
            assertThat(embeddedServer.isRunning()).isTrue();
            assertThat(embeddedServer.getApplicationContext().containsBean(HandlerWithAllAnnotationProperties.class)).isTrue();
        }
    }

    @Test
    void testInvalidAnnotation() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("handlerWithInvalidAnnotationPropertiesEnabled", true);
        assertThrows(DateTimeParseException.class, () -> ApplicationContext.run(EmbeddedServer.class, properties));
    }

}
