package eu.paliwoda.servicepoller.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;

import java.time.Instant;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Pollee - a service which status is checked
 */
@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class Pollee {
    @Id
    Long id;

    String serviceUrl;

    String name;

    Boolean status;

    @Builder.Default
    Instant createdAt = Instant.now();

    Instant lastCheckedAt;

    public boolean canBePolledYet(long pollingInterval, Instant currentTime) {
        checkArgument(pollingInterval >= 0);
        return lastCheckedAt == null ||
                !lastCheckedAt.plusMillis(pollingInterval).isAfter(currentTime);
    }

    public Pollee withPollingResult(PollingResult pollingResult) {
        requireNonNull(pollingResult);
        return toBuilder().status(pollingResult.getHealthy())
                          .lastCheckedAt(pollingResult.getCheckedAt())
                          .build();
    }
}
