package org.cosmin.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.RandomData;
import org.springframework.stereotype.Component;
import io.vavr.control.Try;

import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventGenerator {
    // The event generation will only work if the event classes' package name
    // is the same as the namespace in the avro schema definition

    public <T> T generateEvent(final Class<T> clazz) {
        Schema schema = Try.of(
                () -> (Schema) clazz.getMethod("getClassSchema").invoke(null))
                .getOrElseThrow( ex -> new RuntimeException("Not an Avro class!"));

        final RandomData generator = new RandomData(schema, 1, true);
        final Iterator<Object> it = generator.iterator();
        final String fixture = it.next().toString();
        final DatumReader<T> reader = new SpecificDatumReader<>(schema);

        return Try.of(
                () -> {
                    return reader.read(null, DecoderFactory.get().jsonDecoder(schema, fixture));
                })
                .getOrElse(
                        () -> {
                            log.error("Unable to generate event");
                            return null;
                        }
                );
    }
}
