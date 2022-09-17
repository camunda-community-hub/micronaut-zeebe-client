# micronaut-zeebe-client

[![Release](https://img.shields.io/github/v/release/camunda-community-hub/micronaut-zeebe-client.svg)](https://github.com/camunda-community-hub/micronaut-zeebe-client/releases)
[![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
[![Continuous Integration](https://github.com/camunda-community-hub/micronaut-zeebe-client/workflows/Continuous%20Integration/badge.svg)](https://github.com/camunda-community-hub/micronaut-zeebe-client/actions)
[![GitHub Discussions](https://img.shields.io/badge/Forum-GitHub_Discussions-blue)](https://github.com/camunda-community-hub/micronaut-zeebe-client/discussions)

[![](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)

This open source project allows you to implement a [Zeebe](https://docs.camunda.io/docs/components/zeebe/technical-concepts/architecture/) client with the 
[Micronaut Framework](https://micronaut.io). You can connect to [Camunda Platform 8](https://docs.camunda.io/docs/components/concepts/what-is-camunda-cloud/) (previously Camunda Cloud) or your self-managed Zeebe Cluster.

With this integration you can implement a Zeebe job worker with minimal boilerplate code to process tasks. Additionally, you can use the client to deploy process models, and start and cancel process instances.

The Micronaut Framework is known for its efficient use of resources. If you use GraalVM you have startup times of about 35ms!

The integration is preconfigured with sensible defaults, so that you can get started with minimal configuration: simply add a dependency and your Camunda Platform 8 credentials in your Micronaut project!

If you are interested in using Camunda Platform on a Micronaut application instead, have a look at our open source project [micronaut-camunda-bpm](https://github.com/camunda-community-hub/micronaut-camunda-bpm).

---
_We're not aware of all installations of our Open Source project. However, we love to_
* _listen to your feedback,_
* _discuss possible use cases with you,_
* _align our roadmap to your needs!_

📨 _Please [contact](#contact) us!_

---

Do you want to try it out? Please jump to the [Getting Started](#getting-started) section.

Do you want to contribute to our open source project? Please read the [Contribution Guidelines](CONTRIBUTING.md) and [contact us](#contact).

Micronaut Framework + Camunda Platform 8 = :heart:

# Table of Contents

* ✨ [Features](#features)
* 🚀 [Getting Started](#getting-started)
  * [Dependency Management](#dependency-management)
  * [Creating a Client](#creating-a-client)
  * [ZeebeWorker Annotation](#zeebeworker-annotation)
  * [Configuration](#configuration)
  * [Examples](#examples)
  * [Supported JDKs](#supported-jdks)
* 🏆 [Advanced Topics](#advanced-topics)
  * [Process Tests](#process-tests)
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

## Dependency Management

The Zeebe integration works with both Gradle and Maven, but we recommend using Gradle because it has better Micronaut Support.

You have the following options to integrate the Zeebe integration:
* Create a new Micronaut project using [Micronaut Launch](https://micronaut.io/launch?name=jobworker&features=zeebe) and check that the "zeebe" feature is selected.
* Manually add the dependency to a Micronaut project:
  <details>
  <summary>Click to show Gradle configuration</summary>

  Add the dependency to the build.gradle file:
  ```groovy
  implementation("info.novatec:micronaut-zeebe-client-feature:1.9.0")
  ```
  </details>

  <details>
  <summary>Click to show Maven configuration</summary>

  Add the dependency to the pom.xml file:
  ```xml
  <dependency>
    <groupId>info.novatec</groupId>
    <artifactId>micronaut-zeebe-client-feature</artifactId>
    <version>1.9.0</version>
  </dependency>
  <!-- workaround for https://github.com/camunda-community-hub/micronaut-zeebe-client/issues/88 -->
  <dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.19.3</version>
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

| Property | Description                                                                                               |
|----------|-----------------------------------------------------------------------------------------------------------|
| type     | The mandatory the type of jobs to work on.                                                                |
| timeout  | The optional time for how long a job is exclusively assigned for this worker, e.g "PT15M"                 |
| maxJobsActive  | The optional maximum number of jobs which will be exclusively activated for this worker at the same time. |
| requestTimeout | The optional request timeout for activate job request used to poll for new job, e.g. PT20S.         |
| pollInterval  | The optional maximal interval between polling for new jobs, e.g. PT0.1S for 100ms.                   |

Note: If no value is provided for an optional property then the default will be taken from the configuration as documented below.

## Configuration

You may use the following properties (typically in application.yml) to configure the Zeebe client.

| Prefix                | Property                          | Default       | Description                                                                                                                                                       |
|-----------------------|-----------------------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| zeebe.client.cloud    | .cluster-id                       |               | The cluster ID when connecting to Camunda Platform 8. Don't set this for a local Zeebe Broker.                                                                    |
|                       | .client-id                        |               | The client ID to connect to Camunda Platform 8. Don't set this for a local Zeebe Broker.                                                                               |
|                       | .client-secret                    |               | The client secret to connect to Camunda Platform 8. Don't set this for a local Zeebe Broker.                                                                           |
|                       | .region                           | bru-2         | The region of the Camunda Platform 8 cluster.                                                                                                                          |
|                       | .gateway-address                  | 0.0.0.0:26500 | The gateway address if you're not connecting to Camunda Platform 8. Must be in format host:port.                                                                       |
|                       | .use-plain-text-connection        | true          | Whether to use plain text or a secure connection. This property is not evaluated if connecting to Camunda Platform 8 because that will always use a secure connection. |
|                       | .default-request-timeout          | PT20S         | The request timeout used if not overridden by the command.                                                                                                        |
|                       | .default-job-poll-interval        | 100           | The interval which a job worker is periodically polling for new jobs.                                                                                             |
|                       | .default-job-timeout              | PT5M          | The timeout which is used when none is provided for a job worker.                                                                                                 |
|                       | .default-message-time-to-live     | PT1H          | The time-to-live which is used when none is provided for a message.                                                                                               |
|                       | .default-job-worker-name          | default       | The name of the worker which is used when none is set for a job worker.                                                                                           |
|                       | .num-job-worker-execution-threads | 1             | The number of threads for invocation of job workers. Setting this value to 0 effectively disables subscriptions and workers.                                      |
|                       | .keep-alive                       | PT45S         | Time interval between keep alive messages sent to the gateway.                                                                                                    |
|                       | .ca-certificate-path              | default store | Path to a root CA certificate to be used instead of the certificate in the default keystore.                                                                      |

## Examples
Here are some example applications:
* [Example application](https://github.com/tobiasschaefer/micronaut-zeebe-example) which uses the feature.
* [Internal example application](/micronaut-zeebe-client-example) used during development. Remember that you need to start the [Zeebe Cluster](/micronaut-zeebe-server-example) first.

## Supported JDKs

We officially support the following JDKs:
* JDK 8 (LTS)
* JDK 11 (LTS)
* JDK 17 (LTS)

# 🏆Advanced Topics

## Process Tests

Process tests can be implemented with JUnit 5 and JDK 11 and newer by adding the [Zeebe Process Test](https://github.com/camunda-cloud/zeebe-process-test) library as a dependency:

<details>
<summary>Click to show Gradle dependencies</summary>

```groovy
testImplementation("io.camunda:zeebe-process-test:1.3.0")
```
</details>

<details>
<summary>Click to show Maven dependencies</summary>

```xml
<dependency>
  <groupId>io.camunda</groupId>
  <artifactId>zeebe-process-test</artifactId>
  <version>1.3.0</version>
  <scope>test</scope>
</dependency>
```
</details>

and then implement the unit test with the `@ZeebeProcessTest` but without the `@MicronautTest` annotation:

```java
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.process.test.assertions.ProcessInstanceAssert;
import io.camunda.zeebe.process.test.extensions.ZeebeProcessTest;
import io.camunda.zeebe.process.test.testengine.InMemoryEngine;
import io.camunda.zeebe.process.test.testengine.RecordStreamSource;
import org.junit.jupiter.api.Test;

@ZeebeProcessTest
class ProcessTest {

  InMemoryEngine engine;
  ZeebeClient client;
  @SuppressWarnings("unused")
  RecordStreamSource recordStreamSource;

  @Test
  void workerShouldProcessWork() {

    // Deploy process model
    DeploymentEvent deploymentEvent = client.newDeployCommand()
            .addResourceFromClasspath("bpmn/say_hello.bpmn")
            .send()
            .join();

    BpmnAssert.assertThat(deploymentEvent);

    // Start process instance
    ProcessInstanceEvent event = client.newCreateInstanceCommand()
            .bpmnProcessId("Process_SayHello")
            .latestVersion()
            .send()
            .join();

    engine.waitForIdleState();

    // Verify that process has started
    ProcessInstanceAssert processInstanceAssertions = BpmnAssert.assertThat(event);
    processInstanceAssertions.hasPassedElement("start");
    processInstanceAssertions.isWaitingAtElement("say_hello");

    // Fetch job: say-hello
    ActivateJobsResponse response = client.newActivateJobsCommand()
            .jobType("say-hello")
            .maxJobsToActivate(1)
            .send()
            .join();

    // Complete job: say-hello
    ActivatedJob activatedJob = response.getJobs().get(0);
    client.newCompleteCommand(activatedJob.getKey()).send().join();
    engine.waitForIdleState();

    // ...

    // Verify completed
    engine.waitForIdleState();
    processInstanceAssertions.isCompleted();
  }
}
```

See also a test in our example application: [ProcessTest](/micronaut-zeebe-client-example/src/test/java/info/novatec/micronaut/zeebe/client/example/ProcessTest.java)

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

Install the `gu` executable to be able to install `native-image` based on instructions: https://www.graalvm.org/docs/getting-started/macos/ which links to https://github.com/graalvm/graalvm-ce-builds/releases/latest

```
tar -xvf graalvm-ce-java17-darwin-amd64-21.3.0.tar.gz
sudo mv graalvm-ce-java17-21.3.0 /Library/Java/JavaVirtualMachines
/usr/libexec/java_home -V
sudo xattr -r -d com.apple.quarantine /Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.0
export PATH=/Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.0/Contents/Home/bin:$PATH
gu install native-image
native-image --version
```

### Install GraalVM

Install GraalVM using [SDKMAN!](https://sdkman.io/):

```
curl -s "https://get.sdkman.io" | bash
sdk install java 21.3.0.r17-grl
```

### Initialize Environment

```
sdk use java 21.3.0.r17-grl
export PATH=/Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.0/Contents/Home/bin:$PATH
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java17-21.3.0/Contents/Home
```

### Create Reflection Configuration

```
cd micronaut-zeebe-client-example
../gradlew clean build
mkdir -p src/main/resources/META-INF/native-image
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image/ -jar build/libs/micronaut-zeebe-client-example-0.0.1-SNAPSHOT-all.jar
```

Start the server with the provided docker-compose.yml and cancel the client with `Ctrl-C` once you see that the client is running when it repeatedly logs like `Retrieved value 37. Goodbye, from job 4503599627392427`.

### Build Image

The generated `reflect-config.json` misses three entries (why?) which we add manually:
```json
{
  "name":"io.grpc.util.SecretRoundRobinLoadBalancerProvider$Provider",
  "queryAllPublicMethods":true,
  "methods":[{"name":"<init>","parameterTypes":[] }]
},
{
  "name":"io.grpc.internal.PickFirstLoadBalancerProvider",
  "queryAllPublicMethods":true,
  "methods":[{"name":"<init>","parameterTypes":[] }]
},
{
  "name":"io.grpc.internal.DnsNameResolverProvider",
  "queryAllPublicMethods":true,
  "methods":[{"name":"<init>","parameterTypes":[] }]
},
```

Now build the native image - note: this will take a few minutes:

`../gradlew clean nativeCompile`

### Start Native Client

You can then start the external client (Note: Server must be running):

`build/native/nativeCompile/micronaut-zeebe-client-example`

The application will be up and processing the first tasks in about 35ms (!):

```
INFO  io.micronaut.runtime.Micronaut - Startup completed in 33ms. Server Running: http://localhost:8087
INFO  i.n.m.z.c.example.GreetingHandler - Hello world, from job 2251799813709648
INFO  io.camunda.zeebe.client.job.poller - Activated 1 jobs for worker default and job type say-hello
INFO  i.n.m.z.c.example.GoodbyeHandler - Retrieved value 18. Goodbye, from job 2251799813709653
INFO  io.camunda.zeebe.client.job.poller - Activated 1 jobs for worker default and job type say-goodbye
INFO  i.n.m.z.c.example.GreetingHandler - Hello world, from job 4503599627394811
INFO  io.camunda.zeebe.client.job.poller - Activated 1 jobs for worker default and job type say-hello
```

# 📚Releases

The list of [releases](https://github.com/camunda-community-hub/micronaut-zeebe-client/releases) contains a detailed changelog.

We use [Semantic Versioning](https://semver.org/).

The following compatibility matrix shows the officially supported Micronaut and Zeebe versions for each release.
Other combinations might also work but have not been tested.

| Release | Micronaut Framework | Zeebe |
|---------|---------------------|-------|
| 1.9.0   | 3.6.1               | 8.0.5 |

<details>
<summary>Click to see older releases</summary>

| Release |Micronaut Framework | Zeebe |
|---------|--------|---------|
| 1.8.0   | 3.5.2               | 8.0.3 |
| 1.7.0   | 3.4.1               | 8.0.0 |
| 1.6.0   | 3.4.0               | 1.3.5 |
| 1.5.0   | 3.4.0               | 1.3.5 |
| 1.4.1   | 3.3.4               | 1.3.5 |
| 1.4.0   | 3.3.0     | 1.3.2 |
| 1.3.1   | 3.3.0  | 1.3.2   |
| 1.3.0   | 3.3.0  | 1.3.1   |
| 1.2.2   | 3.2.7  | 1.3.1   |
| 1.2.1   | 3.2.7  | 1.3.1   |
| 1.2.0   | 3.2.6  | 1.3.0   |
| 1.1.1   | 3.2.3  | 1.2.7   |
| 1.1.0   | 3.2.0  | 1.2.4   |
| 1.0.1   | 3.1.3  | 1.2.4   |
| 1.0.0   | 3.1.0  | 1.2.2   |
| 0.0.1   | 3.0.2  | 1.1.3   |

</details>

Download of Releases:
* [GitHub Artifacts](https://github.com/camunda-community-hub/micronaut-zeebe-client/releases)
* [Maven Central Artifacts](https://search.maven.org/artifact/info.novatec/micronaut-zeebe-client-feature)

# 📆Publications

* 2022-02: [Bringing Cloud Native Process Automation to the Micronaut Framework](https://www.novatec-gmbh.de/en/blog/bringing-cloud-native-process-automation-to-micronaut/)  
  Blogpost by Tobias Schäfer and Stefan Schultz

# 📨Contact

If you have any questions or ideas feel free to create an [issue](https://github.com/camunda-community-hub/micronaut-zeebe-client/issues) or contact us via GitHub Discussions or mail.

We love listening to your feedback, and of course also discussing the project roadmap and possible use cases with you!

You can reach us:
* [GitHub Discussions](https://github.com/camunda-community-hub/micronaut-zeebe-client/discussions)
* [mailto:micronaut-camunda@novatec-gmbh.de](mailto:micronaut-camunda@novatec-gmbh.de)

This open source project is being developed by [Novatec Consulting GmbH](https://www.novatec-gmbh.de/en/) with the support of the open source community.

![Novatec Consulting GmbH](novatec.png)
