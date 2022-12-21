package org.cosmin.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmin.avro.TestEvent;
import org.cosmin.generator.EventGenerator;
import org.cosmin.producer.EventSupplier;
import org.cosmin.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectEventTaskScheduler {
    @Value("${producer.direct.delay}")
    private long initialDelay;
    @Value("${producer.direct.interval}")
    private long period;
    @Value("${producer.direct.threads}")
    private int threadPoolSize;

    private final String PRODUCER_BINDING = "eventSupplier-out-0";

    @Autowired
    StreamBridge streamBridge;

    private final EventGenerator eventGenerator;

    @PostConstruct
    public void runTask() {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(threadPoolSize);

        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    final TestEvent event = eventGenerator.generateEvent(TestEvent.class);
                    event.setTimestamp(Instant.now());
                    streamBridge.send(PRODUCER_BINDING, event);
                    log.info("|+++++1+++++|Event sending to Kafka: client id ({}), timestamp ({}), comment ({})",
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
