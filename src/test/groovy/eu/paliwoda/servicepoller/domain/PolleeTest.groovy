package eu.paliwoda.servicepoller.domain

import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

import static eu.paliwoda.servicepoller.fixtures.PolleeFixture.defaultPollee

class PolleeTest extends Specification {

    def "canBePolledYet should thrown IllegalArgumentException when given negative polling interval"() {

        given:
        def pollee = defaultPollee()

        when:
        pollee.canBePolledYet(-1000, Instant.now())

        then:
        thrown(IllegalArgumentException)
    }

    @Unroll
    def "Pollee can be polled when it has not been done yet"() {
        given:
        def pollee = defaultPollee()

        expect:
        !pollee.getLastCheckedAt()

        when:
        boolean result = pollee.canBePolledYet(pollingInterval, Instant.now())

        then:
        result

        where:
        pollingInterval << [0, 1000]
    }

    @Unroll
    def "canBePolledYet returns #expectedResult when #timePassed ms passed and polling interval is set to #pollingInterval"() {
        given:
        def now = Instant.now()
        def lastChecked = now.minusMillis(timePassed)

        def pollee = defaultPollee().toBuilder().lastCheckedAt(lastChecked).build()

        when:
        boolean result = pollee.canBePolledYet(pollingInterval, now)

        then:
        result == expectedResult

        where:
        pollingInterval | timePassed || expectedResult
        0               | 0          || true
        1               | 0          || false
        0               | 1          || true
    }

}
