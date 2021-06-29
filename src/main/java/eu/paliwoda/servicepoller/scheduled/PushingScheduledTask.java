package eu.paliwoda.servicepoller.scheduled;

import eu.paliwoda.servicepoller.domain.WorkDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!integration_test")
public class PushingScheduledTask {

    private final WorkDispatcher workDispatcher;

    public PushingScheduledTask(WorkDispatcher workDispatcher) {
        this.workDispatcher = workDispatcher;
    }

    @Scheduled(fixedDelayString = "${job-queue.pushing-interval}")
    public void runPollingTask() {
        log.info("Running pushing task");
        workDispatcher.pushTasksToQueue();
    }
}
