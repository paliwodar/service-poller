package eu.paliwoda.servicepoller

import eu.paliwoda.servicepoller.repository.PolleeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
@ActiveProfiles("integration_test")
class ServicePollerApplicationTest extends Specification {

    @LocalServerPort
    private Integer port;

    @Autowired
    WebTestClient webTestClient

    @Autowired
    PolleeRepository polleeRepository

    def setup() {
        polleeRepository.deleteAll().subscribe()
    }

    def cleanup() {
        polleeRepository.deleteAll().subscribe()
    }

    def "Testing the e2e flow of adding new service and having them polled"() {
        given: "Added a new service to poll. Which is service poller itself."
        def pollerUrl = "localhost:${port}/services"
        webTestClient.post()
                     .uri("/services?name=Poller&serviceUrl=${pollerUrl}" as String)
                     .exchange()
                     .expectStatus()
                     .isEqualTo(303)

        and: "Dispatching work by pushing it to the message queue"
        webTestClient.post()
                     .uri("/push")
                     .exchange()
                     .expectStatus()
                     .isEqualTo(200)

        expect: "Assuring that the newly added service has not been checked yet"
        webTestClient.get()
                     .uri("/services")
                     .exchange()
                     .expectBody()
                     .xpath("//*[@id=\"pollees\"]/tbody/tr[1]/td[6]").isEqualTo("")

        when: "Triggering services polling action. Normally happens via scheduled tasks, here in tests it is done via specially exposed testing endpoint"
        webTestClient.post()
                     .uri("/poll")
                     .exchange()
                     .expectBody()
                     .jsonPath("\$[:1].status").isEqualTo(true)
                     .jsonPath("\$[:1].name").isEqualTo("Poller")
                     .jsonPath("\$[:1].serviceUrl").isEqualTo(pollerUrl as String)
                     .jsonPath("\$[:1].lastCheckedAt").isNotEmpty()

        then: "Asserting that the service is up"
        webTestClient.get()
                     .uri("/services")
                     .exchange()
                     .expectStatus()
                     .isEqualTo(200)
                     .expectBody()
                     .xpath("//*[@id=\"pollees\"]/tbody/tr[1]/td[2]").isEqualTo("Poller")
                     .xpath("//*[@id=\"pollees\"]/tbody/tr[1]/td[3]").isEqualTo(pollerUrl as String)
                     .xpath("//*[@id=\"pollees\"]/tbody/tr[1]/td[4]").isEqualTo("true")
    }

}
