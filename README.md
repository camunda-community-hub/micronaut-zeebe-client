# micronaut-zeebe-client

This open source project allows you to integrate a [Zeebe](https://docs.camunda.io/docs/components/zeebe/technical-concepts/architecture/) Java Client into 
your [Micronaut](https://micronaut.io) project (similar to [Spring Zeebe](https://github.com/camunda-community-hub/spring-zeebe/)). It allows you to e.g. deploy processes to [Camunda Cloud](https://docs.camunda.io/docs/components/concepts/what-is-camunda-cloud/), start and cancel instances and work 
on [jobs](https://docs.camunda.io/docs/components/concepts/job-workers/).

Micronaut is known for its efficient use of resources. With this integration you can easily implement a Zeebe job worker to process tasks.

The integration is preconfigured with sensible defaults, so that you can get started with minimal configuration: simply add a dependency and your Camunda Cloud credentials in your Micronaut project!

If you are interested in using Camunda Platform on Micronaut instead, have a look at our open source project [micronaut-camunda-bpm](https://github.com/camunda-community-hub/micronaut-camunda-bpm).

---
_We're not aware of all installations of our Open Source project. However, we love to_
* _listen to your feedback,_
* _discuss possible use cases with you,_
* _align our roadmap to your needs!_

📨 _Please [contact](#contact) us!_

---

Do you want to try it out? Please jump to the [Getting Started](#getting-started) section.

Do you want to contribute to our open source project? Please read the [Contribution Guidelines](CONTRIBUTING.md) and [contact us](#contact).

Micronaut + Camunda Cloud = :heart:

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
  * [ZeebeWorker Annotation](#zeebeworker-annotation)
  * [Configuration](#configuration)
* 🏆 [Advanced Topics](#advanced-topics)
  * [Monitoring](#monitoring)  
  * [GraalVM](#graalvm)
* 📚 [Releases](#releases)
* 📆 [Publications](#publications)
* 📨 [Contact](#contact)

# ✨Features
* Camunda external client can be integrated by simply adding a dependency to your project.
* A worker can subscribe to multiple topics.
* The worker's external task client can be configured with [properties](#configuration).

# 🚀Getting Started

This section describes what needs to be done to use `micronaut-zeebe-client-feature` in a Micronaut project.

Here are some example applications:
* [Example application](https://github.com/tobiasschaefer/micronaut-zeebe-example) which uses the feature.
* [Internal example application](/micronaut-zeebe-client-example) used during development. Remember that you need to start the [Zeebe Cluster](/micronaut-zeebe-server-example) first.

## Supported JDKs

We officially support the following JDKs:
* JDK 8 (LTS)
* JDK 11 (LTS)
* JDK 17 (LTS)

## Dependency Management

The Camunda External Worker integration works with both Gradle and Maven, but we recommend using Gradle because it has better Micronaut Support.

You have the following options to integrate the Camunda External Worker integration:
* Create a new Micronaut project using [Micronaut Launch](https://micronaut.io/launch) and select the "camunda-external-worker" feature.
* Manually add the dependency to a Micronaut project:
  <details>
  <summary>Click to show Gradle configuration</summary>

  Add the dependency to the build.gradle file:
  ```groovy
  implementation("info.novatec:micronaut-zeebe-client-feature:1.0.0")
  ```
  </details>

  <details>
  <summary>Click to show Maven configuration</summary>

  Add the dependency to the pom.xml file:
  ```xml
  <dependency>
    <groupId>info.novatec</groupId>
    <artifactId>micronaut-zeebe-client-feature</artifactId>
    <version>1.0.0</version>
  </dependency>
  ```
  </details>

Note: The module `micronaut-zeebe-client-feature` includes the dependency `io.camunda:zeebe-client-java` which will be resolved transitively.

## Creating a Client
The minimal configuration requires you to provide a handler for a specific topic. You can register multiple handlers in this way for different topics. 

On start of the application the external task client will automatically connect to Zeebe and start fetching tasks.

### Option 1: Annotate Method
To register a handler you can annotate a method with `ZeebeWorker`.

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
        
        client.newCompleteCommand(job.getKey()).send()
          .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
    }
}
```


### Option 2: Annotate Class
To register a handler you can annotate a class implementing the `JobHandler` interface with `ZeebeWorker`.

Example handler:
```java 
import info.novatec.micronaut.zeebe.client.feature.ZeebeWorker;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import jakarta.inject.Singleton;

@Singleton
@ZeebeWorker(type = "my-type")
public class ExampleHandler implements JobHandler {

    @Override
    public void handle(JobClient client, ActivatedJob job) {
        // Put your business logic here
        
        client.newCompleteCommand(job.getKey()).send()
          .exceptionally( throwable -> { throw new RuntimeException("Could not complete job " + job, throwable); });
    }
}
```

## ZeebeWorker Annotation
The annotation accepts the following properties, more will be added later:

| Property                    | Default | Description                                                                  |
|-----------------------------|---------|------------------------------------------------------------------------------|
| type                        |         | The mandatory name the client subscribes to                          |

## Configuration

You may use the following properties (typically in application.yml) to configure the Zeebe client.

| Prefix                | Property                          | Default             | Description                                                                                                                   |
|-----------------------|-----------------------------------|---------------------|-------------------------------------------------------------------------------------------------------------------------------|
| zeebe.client.cloud    | .cluster-id                       |                     | The cluster ID when connecting to Camunda Cloud. Don't set this for a local Zeebe Broker.                                     |
|                       | .client-id                        |                     | The client ID to connect to Camunda Cloud. Don't set this for a local Zeebe Broker.                                           |
|                       | .client-secret                    |                     | The client secret to connect to Camunda Cloud. Don't set this for a local Zeebe Broker.                                       |
|                       | .region                           | bru-2               | The region of the Camunda Cloud cluster.                                                                                      |
|                       | .default-request-timeout          | PT20S               | The request timeout used if not overridden by the command.                                                                    |
|                       | .default-job-poll-interval        | 100                 | The interval which a job worker is periodically polling for new jobs.                                                         |
|                       | .default-job-timeout              | PT5M                | The timeout which is used when none is provided for a job worker.                                                             |
|                       | .default-message-time-to-live     | PT1H                | The time-to-live which is used when none is provided for a message.                                                           |
|                       | .default-job-worker-name          | default             | The name of the worker which is used when none is set for a job worker.                                                       |
|                       | .num-job-worker-execution-threads | 1                   | The number of threads for invocation of job workers. Setting this value to 0 effectively disables subscriptions and workers.  |
|                       | .keep-alive                       | PT45S               | Time interval between keep alive messages sent to the gateway.                                                                |
|                       | .gateway-address                  | 0.0.0.0:26500       | The IP socket address of a gateway that the client can initially connect to. Must be in format host:port.                     |
|                       | .ca-certificate-path              | default store       | Path to a root CA certificate to be used instead of the certificate in the default keystore.                                  |

# 🏆Advanced Topics

## Monitoring
Adding a health endpoint for monitoring purposes in a cloud environment can be achieved by adding the dependency:

`runtimeOnly 'io.micronaut:micronaut-management'`

The health endpoint can be retrieved by calling `GET` on `/health`

NOTE: If you don't need a health endpoint you can safely remove the runtime dependency `runtime("netty")` from your project. 
The application will then run as a CLI application without the embedded server.

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

The list of [releases](https://github.com/NovatecConsulting/micronaut-zeebe-client/releases) contains a detailed changelog.

We use [Semantic Versioning](https://semver.org/).

The following compatibility matrix shows the officially supported Micronaut and Zeebe versions for each release.
Other combinations might also work but have not been tested.

| Release |Micronaut | Zeebe |
|--------|--------|--------|
|  1.0.0 | 3.1.0  | 1.2.2  |

<details>
<summary>Click to see older releases</summary>

| Release |Micronaut | Zeebe |
|--------|--------|--------|
|  0.0.1 | 3.0.2  | 1.1.3  |

</details>

Download of Releases:
* [GitHub Artifacts](https://github.com/NovatecConsulting/micronaut-zeebe-client/releases)
* [Maven Central Artifacts](https://search.maven.org/artifact/info.novatec/micronaut-zeebe-client-feature)

# 📆Publications

TODO

# 📨Contact

If you have any questions or ideas feel free to create an [issue](https://github.com/NovatecConsulting/micronaut-zeebe-client/issues) or contact us via GitHub Discussions or mail.

We love listening to your feedback, and of course also discussing the project roadmap and possible use cases with you!

You can reach us:
* [GitHub Discussions](https://github.com/NovatecConsulting/micronaut-zeebe-client/discussions)
* [mailto:micronaut-camunda@novatec-gmbh.de](mailto:micronaut-camunda@novatec-gmbh.de)

This open source project is being developed by [Novatec Consulting GmbH](https://www.novatec-gmbh.de/en/) with the support of the open source community.

![Novatec Consulting GmbH](novatec.jpeg)
