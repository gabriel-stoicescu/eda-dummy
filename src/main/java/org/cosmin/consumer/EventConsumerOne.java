package org.cosmin.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component("eventConsumerOne")
public class EventConsumerOne implements Consumer<TestEvent> {
    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    @Override
    public void accept(final TestEvent event) {
        log.info("|-----1-----| Confluent Cloud Event received: client id ({}), payload ({}), timestamp ({})",
                event.getClientId(),
                event.getPayload(),
                dateTimeFormatter.format(event.getTimestamp()));
    }
}
