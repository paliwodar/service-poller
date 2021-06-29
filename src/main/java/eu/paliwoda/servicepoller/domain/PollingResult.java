package eu.paliwoda.servicepoller.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.time.Instant;

@Value
@AllArgsConstructor
@Builder(toBuilder = true)
public class PollingResult {
    Long polleeId;
    Boolean healthy;
    Instant checkedAt;

    public static PollingResult.PollingResultBuilder ofPollingResponse(Pollee pollee, ClientResponse clientResponse) {
        return PollingResult.builder()
                            .healthy(clientResponse.statusCode().is2xxSuccessful())
                            .polleeId(pollee.getId());
    }

    public static PollingResult.PollingResultBuilder ofNoResponse(Pollee pollee) {
        return PollingResult.builder()
                            .healthy(false)
                            .polleeId(pollee.getId());
    }
}
