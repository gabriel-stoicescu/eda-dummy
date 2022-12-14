package org.cosmin.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.cosmin.avro.TestEvent;
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
    }

    @Override
    public TestEvent get() {
        return eventQueue.poll();
    }
}
