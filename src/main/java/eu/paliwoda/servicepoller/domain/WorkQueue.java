package eu.paliwoda.servicepoller.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A representation of tasks queue. In real life that could be PubSub or Redis.
 */
@Slf4j
@Service
public class WorkQueue {
    Queue<List<Pollee>> queue = new ConcurrentLinkedQueue<>();

    public void push(List<Pollee> batch) {
        checkArgument(!batch.isEmpty());
        log.info("pushing batch of size {} to queue", batch.size());
        queue.offer(batch);
    }

    public Flux<Pollee> pop() {
        return Optional.ofNullable(queue.poll())
                       .map(Flux::fromIterable)
                       .orElse(Flux.just());
    }

    public int size() {
        return queue.size();
    }
}
