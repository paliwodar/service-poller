package eu.paliwoda.servicepoller.controllers;

import eu.paliwoda.servicepoller.domain.Pollee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

/**
 * Sends SSE events about updating the state of polled services so that clients can listen to and update
 */
@Slf4j
@RestController()
public class PolleeUpdatesController {

    public static final String POLLEE_UPDATES_CHANNEL = "POLLEE_UPDATES_CHANNEL";

    private final SubscribableChannel polleeUpdatesChannel;

    public PolleeUpdatesController(@Qualifier(POLLEE_UPDATES_CHANNEL) SubscribableChannel polleeUpdatesChannel) {
        this.polleeUpdatesChannel = polleeUpdatesChannel;
    }

    @GetMapping(path = "/services/updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Pollee> getUpdatesStream() {
        return Flux.create(sink -> {
            MessageHandler handler = message -> sink.next((Pollee) message.getPayload());
            sink.onCancel(() -> polleeUpdatesChannel.unsubscribe(handler));
            polleeUpdatesChannel.subscribe(handler);
        }, FluxSink.OverflowStrategy.LATEST);
    }

    @Bean(POLLEE_UPDATES_CHANNEL)
    static SubscribableChannel getChannel() {
        return MessageChannels.publishSubscribe().get();
    }

}

