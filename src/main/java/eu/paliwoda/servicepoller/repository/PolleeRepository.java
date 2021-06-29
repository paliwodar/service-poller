package eu.paliwoda.servicepoller.repository;

import eu.paliwoda.servicepoller.domain.Pollee;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface PolleeRepository extends ReactiveCrudRepository<Pollee, Long> {

    @Query("SELECT * FROM pollee ORDER BY last_checked_at ASC NULLS FIRST")
    Flux<Pollee> findOldestFirst();

}
