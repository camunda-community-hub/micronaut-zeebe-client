# micronaut-zeebe-client

This open source project allows you to easily integrate [Camunda](https://camunda.com/products/bpmn-engine) 's [External Task Clients](https://docs.camunda.org/manual/latest/user-guide/process-engine/external-tasks/) into [Micronaut](https://micronaut.io) projects.

Micronaut is known for its efficient use of resources. With this integration you can easily implement an external client which to process external tasks. If you use GraalVM you have startup times of about 35ms!

The integration is preconfigured with sensible defaults, so that you can get started with minimal configuration: simply add a dependency in your Micronaut project!

If you also want to run the Camunda Workflow Engine on Micronaut, have a look at the open source project [micronaut-camunda-bpm](https://github.com/camunda-community-hub/micronaut-camunda-bpm).

---
_We're not aware of all installations of our Open Source project. However, we love_
* _listening to your feedback,_
* _discussing possible use cases with you,_
* _aligning the roadmap to your needs!_

📨 _Please [contact](#contact) us!_

---

Do you want to try it out? Please jump to the [Getting Started](#getting-started) section.

Do you want to contribute to our open source project? Please read the [Contribution Guidelines](CONTRIBUTING.md) and [contact us](#contact).

Micronaut + Camunda = :heart:

[![Release](https://img.shields.io/github/v/release/NovatecConsulting/micronaut-zeebe-client.svg)](https://github.com/NovatecConsulting/micronaut-zeebe-client/releases)
[![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Continuous Integration](https://github.com/NovatecConsulting/micronaut-zeebe-client/workflows/Continuous%20Integration/badge.svg)](https://github.com/NovatecConsulting/micronaut-zeebe-client/actions)
[![GitHub Discussions](https://img.shields.io/badge/Forum-GitHub_Discussions-blue)](https://github.com/NovatecConsulting/micronaut-zeebe-client/discussions)

[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)

# Table of Contents

* ✨ [Features](#features)
* 🚀 [Getting Started](#getting-started)
  * [Supported JDKs](#supported-jdks)
  * [Dependency Management](#dependency-management)
  * [Creating a Client](#creating-a-client)
  * [ExternalTaskSubscription Annotation](#externaltasksubscription-annotation)
  * [Configuration](#configuration)
* 🏆 [Advanced Topics](#advanced-topics)
  * [Customize the External Task Client](#customize-the-external-task-client)
  * [GraalVM](#graalvm)
* 📚 [Releases](#releases)
* 📆 [Publications](#publications)
* 📨 [Contact](#contact)

# ✨Features
* Camunda external client can be integrated by simply adding a dependency to your project.
* A worker can subscribe to multiple topics.
* The worker's external task client can be configured with [properties](#configuration) and [programmatically](#customize-the-external-task-client).

# 🚀Getting Started

This section describes what needs to be done to use `micronaut-zeebe-client-feature` in a Micronaut project.

Here are some example applications:
* [Internal example application](/micronaut-zeebe-client-example) used during development. Remember that you need to start the [Zeebe Cluster](/micronaut-zeebe-server-example) first.

## Supported JDKs

We officially support the following JDKs:
* JDK 8 (LTS)
* JDK 11 (LTS)
* JDK 16 (the latest version supported by Micronaut)

## Dependency Management

The Camunda External Worker integration works with both Gradle and Maven, but we recommend using Gradle because it has better Micronaut Support.

You have the following options to integrate the Camunda External Worker integration:
* Create a new Micronaut project using [Micronaut Launch](https://micronaut.io/launch) and select the "camunda-external-worker" feature.
* Manually add the dependency to a Micronaut project:
  <details>
  <summary>Click to show Gradle configuration</summary>

  Add the dependency to the build.gradle file:
  ```groovy
  implementation("info.novatec:micronaut-zeebe-client-feature:0.0.1")
  ```
  </details>

  <details>
  <summary>Click to show Maven configuration</summary>

  Add the dependency to the pom.xml file:
  ```xml
  <dependency>
    <groupId>info.novatec</groupId>
    <artifactId>micronaut-zeebe-client-feature</artifactId>
    <version>0.0.1</version>
  </dependency>
  ```
  </details>

Note: The module `micronaut-zeebe-client-feature` includes the dependency `io.camunda:zeebe-client-java` which will be resolved transitively.

## Creating a Client
The minimal configuration requires you to provide a handler for a specific topic. You can register multiple handlers in this way for different topics. 
To register a handler you just need to add the annotation `ZeebeWorker` and specify the type to listen to. On start of the application the external task 
client will automatically connect to zeebe and start fetching tasks.

Example handler:
```java 
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import jakarta.inject.Singleton;

@Singleton
public class ExampleHandler {

    @ZeebeWorker(type = "my-type")
    public void doSomething(JobClient client, ActivatedJob job) {
        // Put your business logic here
        
        client.newCompleteCommand(job.getKey()).send().join();
    }
}
```

## ZeebeWorker Annotation
The annotation accepts the following properties, more will be added later:

| Property                    | Default | Description                                                                  |
|-----------------------------|---------|------------------------------------------------------------------------------|
| type                        |         | The mandatory name the client subscribes to                          |

## Configuration
```java
@Singleton
@ZeebeWorker(type = "my-topic")
public class SimpleHandler {
    ...
}
```

# 🏆Advanced Topic

## GraalVM

With [GraalVM](https://www.graalvm.org/) you can reduce start-up time and memory usage even more! For example, on a developer environment the start-up time will drop to about 35ms!

The following instructions are based on macOS - other operating systems will probably be similar. Feel free to create a pull request with updated instructions for other operating systems.

### Initial Setup

Install the `gu` executable to be able to install `native-image` based on instructions: https://www.graalvm.org/docs/getting-started/macos/

```
tar -xvf graalvm-ce-java11-darwin-amd64-21.0.0.2.tar.gz
sudo mv graalvm-ce-java11-21.0.0.2 /Library/Java/JavaVirtualMachines
/usr/libexec/java_home -V
gu install native-image
```

### Install GraalVM

TODO

# 📚Releases

TODO

# 📆Publications

TODO

# 📨Contact

This open source project is being developed by [Novatec Consulting GmbH](https://www.novatec-gmbh.de/en/) with the support of the open source community.

If you have any questions or ideas feel free to create an [issue](https://github.com/NovatecConsulting/micronaut-zeebe-client/issues) or contact us via GitHub Discussions or mail.

We love listening to your feedback, and of course also discussing the project roadmap and possible use cases with you!

You can reach us:
* [GitHub Discussions](https://github.com/NovatecConsulting/micronaut-zeebe-client/discussions)
* [mailto:micronaut-camunda@novatec-gmbh.de](mailto:micronaut-camunda@novatec-gmbh.de)
