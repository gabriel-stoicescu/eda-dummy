package org.cosmin.producer;

import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.cosmin.util.Constants;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Slf4j
@Component
public class EventSupplier implements Supplier<TestEvent> {
    private final BlockingQueue<TestEvent> eventQueue = new LinkedBlockingQueue<>();

    public void produce(final TestEvent msg) {
        eventQueue.add(msg);
        log.debug("Blocking queue after adding event: size - {} | remaining capacity - {}",
                eventQueue.size(),
                eventQueue.remainingCapacity());
    }

    @Override
    public TestEvent get() {
        TestEvent event = eventQueue.poll();

        if (null != event) {
            log.info("|+++++1+++++|Event sending to Kafka: client id ({}), timestamp ({}), comment ({})",
                    event.getClientId(),
                    Constants.DATE_TIME_FORMAT.format(event.getTimestamp()),
                    event.getComment());
        }

        return event;
    }
}
