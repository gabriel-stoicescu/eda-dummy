package org.cosmin.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
public class DirectEventProducer {
    private final String PRODUCER_BINDING = "eventSupplier-out-0";

    @Autowired
    StreamBridge streamBridge;

    public <T> void produce(T event) {
        streamBridge.send(PRODUCER_BINDING, event);
    }
}
