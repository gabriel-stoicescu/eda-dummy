package org.cosmin.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericData;
import org.cosmin.avro.TestEvent;
import org.cosmin.generator.EventGenerator;
import org.cosmin.generator.TestEventGenerator;
import org.cosmin.producer.EventSupplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
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

    private final TestEventGenerator testEventGenerator;

    private final EventSupplier eventSupplier;

    private final Random random = new Random(System.currentTimeMillis());

    @PostConstruct
    public void runTask() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPoolSize);

        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    final GenericData.Record event = testEventGenerator.generateEvent();
                    eventSupplier.produce(event);
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
