package org.cosmin.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.cosmin.util.Constants;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component("eventConsumerTwo")
public class EventConsumerTwo implements Consumer<TestEvent> {
    @Override
    public void accept(final TestEvent event) {
        log.info("|-----2-----| Confluent Cloud Event received: client id ({}), payload ({}), timestamp ({})",
                event.getClientId(),
                event.getPayload(),
                Constants.DATE_TIME_FORMAT.format(event.getTimestamp()));
    }
}
