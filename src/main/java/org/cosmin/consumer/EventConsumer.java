package org.cosmin.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component("eventConsumer")
public class EventConsumer implements Consumer<TestEvent> {
    @Override
    public void accept(final TestEvent event) {
        log.info("Confluent Cloud Event received: client id ({}), payload ({}), timestamp ({})",
                event.getClientId(),
                event.getPayload(),
                event.getTimestamp());
    }
}
