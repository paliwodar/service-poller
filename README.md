# Description

A simple service poller that keeps a list of
services (defined by a URL), and periodically does a HTTP GET to each and
saves the response ("OK" or "FAIL"). Apart from the polling logic 
all the services are visualised and easily managed in a basic UI presenting
the all services together with their status.

Additional features:
* the results from the poller are automatically shown to the user
* the poller is protected from misbehaving services (for example answering
  really slowly)
* simultaneous writes should not cause undesired behaviour  

# Getting Started

Starting the service: `./gradlew bootRun`

Showing the UI: `localhost:8080/services`


## Backlog

* Adding a better frontend
* Integration with Redis or such instead of local ad-hoc message queue abstraction
* braking down into separate services

## Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.1/gradle-plugin/reference/html/)

