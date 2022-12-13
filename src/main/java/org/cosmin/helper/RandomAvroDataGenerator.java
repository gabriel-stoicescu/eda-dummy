package org.cosmin.helper;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RandomAvroDataGenerator implements Iterable<Object> {
    private static final String DECIMAL = "decimal";
    private static final String UUID = "uuid";
    private static final String DATE = "date";
    private static final String TIME_MILLIS = "time-millis";
    private static final String TIME_MICROS = "time-micros";
    private static final String LOCAL_TIME_MILLIS = "local-time-millis";
    private static final String LOCAL_TIME_MICROS = "local-time-micros";
    private static final String PRECISION_PROP = "precision";
    private static final String SCALE_PROP = "scale";
    private final Schema root;
    private final long seed;
    private final int count;
    private final boolean utf8ForString;

    public RandomAvroDataGenerator(Schema schema, int count, boolean utf8ForString) {
        this(schema, count, System.currentTimeMillis(), utf8ForString);
    }

    public RandomAvroDataGenerator(Schema schema, int count, long seed, boolean utf8ForString) {
        this.root = schema;
        this.seed = seed;
        this.count = count;
        this.utf8ForString = utf8ForString;
    }

    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            private int n;
            private final Random random;

            {
                this.random = new Random(RandomAvroDataGenerator.this.seed);
            }

            public boolean hasNext() {
                return this.n < RandomAvroDataGenerator.this.count;
            }

            public Object next() {
                ++this.n;
                return RandomAvroDataGenerator.this.generate(RandomAvroDataGenerator.this.root, this.random, 0);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    private Object generate(Schema schema, Random random, int d) {
        int length;
        switch (schema.getType()) {
            case RECORD:
                SpecificData.Record record = new SpecificData.Record(schema);
                Iterator var13 = schema.getFields().iterator();

                while (var13.hasNext()) {
                    Schema.Field field = (Schema.Field) var13.next();
                    Object value = this.generate(field.schema(), random, d + 1);
                    record.put(field.name(), value);
                }
                return record;
            case ENUM:
                List<String> symbols = schema.getEnumSymbols();
                return new GenericData.EnumSymbol(schema, (String) symbols.get(random.nextInt(symbols.size())));
            case ARRAY:
                length = random.nextInt(5) + 2 - d;
                if (length == 0) {
                    length = 1;
                }

                GenericArray<Object> array = new GenericData.Array(length <= 0 ? 0 : length, schema);

                for(int i = 0; i < length; ++i) {
                    array.add(this.generate(schema.getElementType(), random, d + 1));
                }

                return array;
            case MAP:
                length = random.nextInt(5) + 2 - d;
                if (length == 0) {
                    length = 1;
                }

                Map<Object, Object> map = new HashMap<>(length <= 0 ? 0 : length);

                for(int i = 0; i < length; ++i) {
                    map.put(this.randomString(random, 40), this.generate(schema.getValueType(), random, d + 1));
                }
                return map;
            case UNION:
                List<Schema> types = schema.getTypes();
                Schema type = (Schema)types.get(random.nextInt(types.size()));
                if (type.getType() == Schema.Type.NULL) {
                    type = (Schema)types.get(1);
                }

                Map<Object, Object> union = new HashMap<>(1);
                if (type.getType() != Schema.Type.RECORD &&
                        type.getType() != Schema.Type.ENUM &&
                        type.getType() != Schema.Type.ARRAY &&
                        type.getType() != Schema.Type.MAP &&
                        type.getType() != Schema.Type.UNION) {
                    union.put(type.getType().getName(), this.generate(type, random, d));
                } else {
                    union.put(type.getFullName(), this.generate(type, random, d));
                }
                return union;
            case FIXED:
                byte[] bytes = new byte[schema.getFixedSize()];
                random.nextBytes(bytes);
                return new GenericData.Fixed(schema, bytes);
            case STRING:
                return this.randomString(random, 40);
            case BYTES:
                return randomBytes(random, 40, schema.getProp("logicalType"), schema);
            case INT:
                return random.nextInt();
            case LONG:
                return this.randomLong(random, schema.getProp("logicalType"));
            case FLOAT:
                return random.nextFloat();
            case DOUBLE:
                return random.nextDouble();
            case BOOLEAN:
                return random.nextBoolean();
            case NULL:
                return null;
            default:
                throw new RuntimeException("Unknown type: " + schema);
        }
    }

    private Object randomString(Random random, int maxLength) {
        int length = random.nextInt(maxLength);

        if (length == 0) {
            ++length;
        }

        byte[] bytes = new byte[length];

        for(int i = 0; i < length; ++i) {
            bytes[i] = (byte)(97 + random.nextInt(25));
        }

        return this.utf8ForString ? new Utf8(bytes) : new String(bytes, StandardCharsets.UTF_8);
    }

    private Object randomLong(Random random, String logicalType) {
        if (logicalType == null) {
            return random.nextLong();
        } else {
            switch (logicalType) {
                case "uuid":
                    return java.util.UUID.randomUUID().node();
                case "date":
                    return Instant.now().truncatedTo(ChronoUnit.DAYS).toEpochMilli();
                case "timestamp-millis":
                case "local-timestamp-millis":
                    return Instant.now().truncatedTo(ChronoUnit.MILLIS).toEpochMilli();
                case "timestamp-micros":
                case "local-timestamp-micros":
                    return Instant.now().truncatedTo(ChronoUnit.MICROS).toEpochMilli() * 1000L;
                default:
                    return random.nextLong();
            }
        }
    }

    private static Object randomBytes(Random random, int maxLength, String logicalType, Schema schema) {
        if (logicalType == null) {
            ByteBuffer bytes = ByteBuffer.allocate(random.nextInt(maxLength));
            bytes.limit(bytes.capacity());
            random.nextBytes(bytes.array());
            return new String(bytes.array(), StandardCharsets.UTF_8);
        } else {
            switch (logicalType) {
                case "decimal":
                    Integer scale = (Integer) schema.getObjectProp("scale");
                    Integer precision = (Integer) schema.getObjectProp("precision");
                    if (scale != null && precision != null) {
                        return new String(
                                (new BigDecimal(new BigInteger(40, random),
                                        scale,
                                        new MathContext(precision, RoundingMode.DOWN))).unscaledValue().toByteArray(),
                                StandardCharsets.UTF_8);
                    }

                    return new String(
                            (new BigDecimal(new BigInteger(40, random))).unscaledValue().toByteArray(),
                            StandardCharsets.UTF_8);
                default:
                    ByteBuffer bytes = ByteBuffer.allocate(random.nextInt(maxLength));
                    bytes.limit(bytes.capacity());
                    random.nextBytes(bytes.array());
                    return new String(bytes.array(), StandardCharsets.UTF_8);
            }
        }
    }
}
