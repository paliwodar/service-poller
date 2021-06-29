package eu.paliwoda.servicepoller.domain;

import eu.paliwoda.servicepoller.repository.PolleeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;

/**
 * Getting a task (stream of services) and polling their status. For now there's an assumption
 * the tasks are ordered by last_checked_at.
 */
@Slf4j
@Service
public class Poller {

    private final WorkQueue workQueue;
    private final PolleeRepository polleeRepository;
    private final HealthChecker healthChecker;
    private final long pollingInterval;
    private final Clock clock;

    public Poller(WorkQueue workQueue, PolleeRepository polleeRepository,
                  HealthChecker healthChecker,
                  @Value("${polling.interval}") long pollingInterval,
                  Clock clock) {
        this.workQueue = workQueue;
        this.polleeRepository = polleeRepository;
        this.healthChecker = healthChecker;
        this.pollingInterval = pollingInterval;
        this.clock = clock;
    }

    public Flux<Pollee> poll() {
        return workQueue.pop()
                        .takeWhile(pollee -> pollee.canBePolledYet(pollingInterval, Instant.now(clock)))
                        .flatMap(healthChecker::check)
                        .flatMap(this::updatePollee);
    }

    @Transactional
    Mono<Pollee> updatePollee(PollingResult pollingResult) {
        return polleeRepository.findById(pollingResult.getPolleeId())
                               .flatMap(pollee -> polleeRepository.save(pollee.withPollingResult(pollingResult)));
    }

    @Bean
    public static Clock getClock() {
        return Clock.systemDefaultZone();
    }

}
