package eu.paliwoda.servicepoller.domain

import eu.paliwoda.servicepoller.web.WebClientService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit

import static eu.paliwoda.servicepoller.fixtures.PolleeFixture.defaultPollee

@ActiveProfiles("integration_test")
class HealthCheckerTest extends Specification {

    public static final Instant fixedNow = Instant.now()

    @Shared
    MockWebServer mockWebServer

    def webClientService = new WebClientService()

    @Subject
    def healthChecker = new HealthChecker(2000, webClientService, Clock.fixed(fixedNow, ZoneId.systemDefault()))

    def setup() {
        mockWebServer = new MockWebServer()
        mockWebServer.start()
    }

    def cleanup() {
        mockWebServer.shutdown()
    }

    @Unroll
    def "check should return #expectedHealthy when getting #statusCode status from polled service"() {
        given:
        def pollee = defaultPollee(mockWebServer.url("").toString())
        mockWebServer.enqueue(new MockResponse().setResponseCode(statusCode))

        when:
        def pollingResult = healthChecker.check(pollee).block()

        then:
        pollingResult.getHealthy() == expectedHealthy
        pollingResult.getCheckedAt() == fixedNow

        where:
        statusCode || expectedHealthy
        200        || true
        404        || false
        500        || false
    }

    def "check should return false when getting a timeout from polled service"() {
        given:
        def pollee = defaultPollee(mockWebServer.url("").toString())
        mockWebServer.enqueue(new MockResponse().setBodyDelay(5000L, TimeUnit.MILLISECONDS)
                                                .setHeadersDelay(5000L, TimeUnit.MILLISECONDS))

        when:
        def pollingResult = healthChecker.check(pollee).block()

        then:
        !pollingResult.getHealthy()
        pollingResult.getCheckedAt() == fixedNow
    }

    @Unroll
    def "check returns unhealthy if host is unreachable at given port"() {
        given:
        def pollee = defaultPollee(url)

        when:
        def pollingResult = healthChecker.check(pollee).block()

        then:
        !pollingResult.getHealthy()
        pollingResult.getCheckedAt() == fixedNow

        where:
        url << ["malformed.=", "localhost:1"]
    }

}
