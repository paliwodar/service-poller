package eu.paliwoda.servicepoller.api;

import eu.paliwoda.servicepoller.domain.Pollee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PolleeDto {
    String serviceUrl;
    String name;

    public Pollee toDomain() {
        return Pollee.builder()
                     .serviceUrl(serviceUrl)
                     .name(name)
                     .build();
    }
}
