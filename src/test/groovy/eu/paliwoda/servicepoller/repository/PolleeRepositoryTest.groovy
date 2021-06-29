package eu.paliwoda.servicepoller.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.Instant

import static eu.paliwoda.servicepoller.fixtures.PolleeFixture.defaultPollee

@DataR2dbcTest
@ActiveProfiles("integration_test")
class PolleeRepositoryTest extends Specification {

    def fixedInstant = Instant.now()

    @Autowired
    private PolleeRepository polleeRepository

    def setup() {
        polleeRepository.deleteAll().subscribe()
    }

    def cleanup() {
        polleeRepository.deleteAll().subscribe()
    }

    def "findOldestFirst returns results ordered by last_checked_at"() {
        given:
        def polles = [defaultPollee("pollee_1").toBuilder().lastCheckedAt(fixedInstant).build(),
                      defaultPollee("pollee_2").toBuilder().lastCheckedAt(fixedInstant.plusSeconds(2)).build(),
                      defaultPollee("pollee_3").toBuilder().lastCheckedAt(null).build(),
                      defaultPollee("pollee_4").toBuilder().lastCheckedAt(fixedInstant.minusSeconds(3)).build()]

        and:
        polleeRepository.saveAll(polles).subscribe()

        expect:
        polleeRepository.findOldestFirst().toIterable().asList().serviceUrl == ["pollee_3", "pollee_4", "pollee_1", "pollee_2"]
    }
}
