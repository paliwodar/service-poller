package eu.paliwoda.servicepoller.controllers;

import eu.paliwoda.servicepoller.api.PolleeDto;
import eu.paliwoda.servicepoller.repository.PolleeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import static eu.paliwoda.servicepoller.controllers.PolleeUpdatesController.POLLEE_UPDATES_CHANNEL;

/**
 * Exposing endpoins for Thymeleaf view integration. In real life there would be a REST API for adding/updating/... and getting all the services using JSON.
 *
 */
@Slf4j
@Controller
@RequestMapping(value = "/services")
public class ViewController {

    private final PolleeRepository polleeRepository;

    private final SubscribableChannel polleeUpdatesChannel;

    public ViewController(PolleeRepository polleeRepository,
                          @Qualifier(POLLEE_UPDATES_CHANNEL) SubscribableChannel polleeUpdatesChannel) {
        this.polleeRepository = polleeRepository;
        this.polleeUpdatesChannel = polleeUpdatesChannel;
    }

    @PostMapping
    public String add(@ModelAttribute PolleeDto pollee, Model model) {
        log.info("Adding new service: {}", pollee);

        polleeRepository.save(pollee.toDomain())
                        .subscribe(savedPollee -> {
                            log.info("Saved: {}", savedPollee);
                            polleeUpdatesChannel.send(new GenericMessage<>(savedPollee));
                        });
        return "redirect:/services";
    }

    @GetMapping
    public String pollees(Model model) {
        IReactiveDataDriverContextVariable dataDriverContextVariable =
                new ReactiveDataDriverContextVariable(this.polleeRepository.findAll(), 128);

        model.addAttribute("polleeDto", new PolleeDto());
        model.addAttribute("pollees", dataDriverContextVariable);
        return "home";
    }

}

