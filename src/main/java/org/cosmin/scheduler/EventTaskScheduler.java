package org.cosmin.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.cosmin.generator.EventGenerator;
import org.cosmin.producer.EventSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventTaskScheduler {
    @Value("${producer.delay}")
    private long initialDelay;
    @Value("${producer.interval}")
    private long period;
    @Value("${producer.threads}")
    private int threadPoolSize;

    private final EventGenerator eventGenerator;
    private final EventSupplier eventSupplier;

    private final Random random = new Random(System.currentTimeMillis());

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @PostConstruct
    public void runTask() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPoolSize);

        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    final TestEvent event = eventGenerator.generateEvent(TestEvent.class);
                    event.setTimestamp(Instant.now());
                    eventSupplier.produce(event);
                    log.info("|++++++++++|Event generated: client id ({}), timestamp ({}), comment ({})",
                            event.getClientId(),
                            dateTimeFormatter.format(event.getTimestamp()),
                            event.getComment());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },
            initialDelay,
            period,
            TimeUnit.MILLISECONDS
        );
    }

    private TestEvent sampleEvent() {

        return TestEvent.newBuilder()
                .setClientId(String.valueOf(random.nextLong()))
                .setPayload("Dummy payload generated at " + Instant.now().toString())
                .setTimestamp(Instant.now())
                .build();
    }
}
