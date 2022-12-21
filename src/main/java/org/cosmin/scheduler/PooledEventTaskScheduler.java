package org.cosmin.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.cosmin.generator.EventGenerator;
import org.cosmin.producer.EventSupplier;
import org.cosmin.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PooledEventTaskScheduler {
    @Value("${producer.pooled.delay}")
    private long initialDelay;
    @Value("${producer.pooled.interval}")
    private long period;
    @Value("${producer.pooled.threads}")
    private int threadPoolSize;

    private final EventGenerator eventGenerator;
    private final EventSupplier eventSupplier;

    @PostConstruct
    public void runTask() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPoolSize);

        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    final TestEvent event = eventGenerator.generateEvent(TestEvent.class);
                    event.setTimestamp(Instant.now());
                    eventSupplier.produce(event);
                    log.info("|     0     |Event queued: client id ({}), timestamp ({}), comment ({})",
                            event.getClientId(),
                            Constants.DATE_TIME_FORMAT.format(event.getTimestamp()),
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
}
