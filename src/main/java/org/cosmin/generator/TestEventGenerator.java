package org.cosmin.generator;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.RandomData;
import org.cosmin.avro.TestEvent;
import org.cosmin.helper.RandomAvroDataGenerator;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestEventGenerator {

    public GenericData.Record generateEvent() {
        Schema schema = TestEvent.getClassSchema();

        final RandomAvroDataGenerator generator = new RandomAvroDataGenerator(schema, 1, true);
        final Iterator<Object> it = generator.iterator();
        final Object generated = it.next();
        System.out.println("I've generated: " + generated.getClass().getName());
        return (GenericData.Record) generated;
    }
}
