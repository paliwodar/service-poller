package eu.paliwoda.servicepoller.controllers;

import eu.paliwoda.servicepoller.domain.Pollee;
import eu.paliwoda.servicepoller.domain.Poller;
import eu.paliwoda.servicepoller.domain.WorkDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Class created for testing purposes. It's allowing to run scheduled tasks actions in a predictable way.
 */
@Slf4j
@RestController
@Profile("integration_test")
public class TestingTasksController {

    private final Poller poller;

    private final WorkDispatcher workDispatcher;

    public TestingTasksController(Poller poller, WorkDispatcher workDispatcher) {
        this.poller = poller;
        this.workDispatcher = workDispatcher;
    }

    @PostMapping(value = "/poll")
    public Flux<Pollee> poll() {
        return poller.poll();
    }

    @PostMapping(value = "/push")
    public void push() {
        workDispatcher.pushTasksToQueue();
    }

}

