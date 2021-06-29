package eu.paliwoda.servicepoller.domain;

import eu.paliwoda.servicepoller.repository.PolleeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A class resposible for producing batch polling tasks in a form of queue of list of pollees.
 *
 * Currently here it is triggered by a scheduled tasks. In reality I'd have it as a separate service,
 * that's reading from db and pushing to a message queue, serverless perhaps.
 *
 * We prevent here the queue from growing too much. It could be done in many different ways.
 *
 * Also perhaps we could have better mechanism to make sure the services are polled more evenly.
 */
@Slf4j
@Component
public class WorkDispatcher {

    private final int batchSize;

    private final PolleeRepository polleeRepository;

    private final WorkQueue workQueue;

    public WorkDispatcher(@Value("${job-queue.batch-size}") int batchSize,
                          PolleeRepository polleeRepository,
                          WorkQueue workQueue) {
        this.batchSize = batchSize;
        this.polleeRepository = polleeRepository;
        this.workQueue = workQueue;
    }

    public void pushTasksToQueue() {
        polleeRepository.count()
                        .filter(this::needsMoreWork)
                        .flatMapMany(count -> polleeRepository.findOldestFirst())
                        .buffer(batchSize)
                        .subscribe(workQueue::push);
    }

    private boolean needsMoreWork(Long count) {
        return Math.ceil((double) count / batchSize) > workQueue.size();
    }

}
