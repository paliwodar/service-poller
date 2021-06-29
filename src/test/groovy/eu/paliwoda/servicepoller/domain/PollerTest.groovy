package eu.paliwoda.servicepoller.domain


import eu.paliwoda.servicepoller.repository.PolleeRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification
import spock.lang.Subject

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static eu.paliwoda.servicepoller.fixtures.PolleeFixture.existingPollee

class PollerTest extends Specification {

    def fixedNow = Instant.now()
    def fixedClock = Clock.fixed(fixedNow, ZoneId.systemDefault())
    def pollee = existingPollee()

    def healthChecker = Mock(HealthChecker)

    def workQueue = Mock(WorkQueue) {
        pop() >> Flux.just(pollee)
    }

    def polleeRepository = Mock(PolleeRepository) {
        findById(pollee.getId()) >> Mono.just(pollee)
    }

    @Subject
    def poller = new Poller(workQueue, polleeRepository, healthChecker, 0, fixedClock)

    def "poll should return saved pollee which has been checkted at given time"() {
        given:
        healthChecker.check(_ as Pollee) >> { arguments ->
            Mono.just(PollingResult.ofNoResponse(arguments[0] as Pollee).checkedAt(fixedNow).build())
        }

        expect:
        !pollee.lastCheckedAt

        when:
        def result = poller.poll().blockLast()

        then:
        !result.status
        result.lastCheckedAt == fixedNow
        1 * polleeRepository.save(_ as Pollee) >> { arguments -> Mono.just(arguments[0] as Pollee) }
    }
}
