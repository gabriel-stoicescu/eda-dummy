package org.cosmin.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;

@Slf4j
@Component
public class EventSupplier implements Supplier<GenericData.Record> {
    private final BlockingQueue<GenericData.Record> eventQueue = new LinkedBlockingQueue<>();

    public void produce(final GenericData.Record msg) {
        eventQueue.add(msg);
    }

    @Override
    public GenericData.Record get() {
        return eventQueue.poll();
    }
}
