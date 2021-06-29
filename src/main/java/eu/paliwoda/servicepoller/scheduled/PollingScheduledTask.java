package eu.paliwoda.servicepoller.scheduled;

import eu.paliwoda.servicepoller.domain.Poller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static eu.paliwoda.servicepoller.controllers.PolleeUpdatesController.POLLEE_UPDATES_CHANNEL;

/**
 *
 */
@Slf4j
@Component
@Profile("!integration_test")
public class PollingScheduledTask {

    private final Poller poller;

    private final SubscribableChannel polleeUpdatesChannel;

    public PollingScheduledTask(Poller poller, @Qualifier(POLLEE_UPDATES_CHANNEL) SubscribableChannel polleeUpdatesChannel) {
        this.poller = poller;
        this.polleeUpdatesChannel = polleeUpdatesChannel;
    }

    @Scheduled(fixedDelayString = "${polling.fixed-delay}")
    public void runPollingTask() {
        log.info("Running polling task");
        poller.poll()
              .subscribe(pollee -> {
                  log.info("Pollee updated: {}", pollee);
                  polleeUpdatesChannel.send(new GenericMessage<>(pollee));
              });
    }
}
