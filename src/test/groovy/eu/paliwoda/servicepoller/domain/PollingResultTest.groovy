package eu.paliwoda.servicepoller.domain

import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import spock.lang.Specification
import spock.lang.Unroll

import static eu.paliwoda.servicepoller.fixtures.PolleeFixture.existingPollee

class PollingResultTest extends Specification {

    def "ofNoResponse should set id and healthy as false"() {
        given:
        def pollee = existingPollee()

        when:
        def pollingResult = PollingResult.ofNoResponse(pollee).build()

        then:
        !pollingResult.getHealthy()
        pollingResult.polleeId == pollee.getId()
    }

    @Unroll
    def "ofPollingResponse should set id and healthy as #expectedHealthy"() {
        given:
        def pollee = existingPollee()
        def clientResponse = Mock(ClientResponse) {
            statusCode() >> clientStatusCode
        }

        when:
        def pollingResult = PollingResult.ofPollingResponse(pollee, clientResponse).build()

        then:
        pollingResult.getHealthy() == expectedHealthy
        pollingResult.polleeId == pollee.getId()

        where:
        clientStatusCode                 || expectedHealthy
        HttpStatus.ACCEPTED              || true
        HttpStatus.NOT_FOUND             || false
        HttpStatus.INTERNAL_SERVER_ERROR || false
    }

}
