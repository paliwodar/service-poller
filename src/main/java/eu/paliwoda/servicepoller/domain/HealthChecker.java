package eu.paliwoda.servicepoller.domain;

import eu.paliwoda.servicepoller.web.WebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Performs a get call to given service. Should gracefully handle cases of timeouts and such.
 */
@Slf4j
@Service
public class HealthChecker {

    private final long pollingTimeout;

    private final WebClientService webClientService;

    private final Clock clock;

    public HealthChecker(@Value("${polling.timeout}") long pollingTimeout,
                         WebClientService webClientService,
                         Clock clock) {
        this.pollingTimeout = pollingTimeout;
        this.webClientService = webClientService;
        this.clock = clock;
    }

    public Mono<PollingResult> check(Pollee pollee) {
        Objects.requireNonNull(pollee);

        Instant now = Instant.now(clock);
        PollingResult.PollingResultBuilder fallbackResponse = PollingResult.ofNoResponse(pollee);

        return webClientService.get(pollee.getServiceUrl())
                               .exchangeToMono(response -> Mono.just(PollingResult.ofPollingResponse(pollee, response)))
                               .timeout(Duration.ofMillis(pollingTimeout), Mono.just(fallbackResponse))
                               .onErrorResume(e -> Mono.just(fallbackResponse))
                               .map(pollingResultBuilder -> pollingResultBuilder.checkedAt(now).build());
    }

}
