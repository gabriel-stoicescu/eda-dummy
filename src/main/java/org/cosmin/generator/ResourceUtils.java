package org.cosmin.generator;

import io.vavr.control.Try;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@UtilityClass
public class ResourceUtils {
    private static final String PATH = "fixtures/";

    private static final String PATTERN = "classpath*:/fixtures/*";

    private static final String EXTENSION = ".json";

    private static final Map<String, String> fixtures = new HashMap<>();

    static {
        Try.run(
                () -> {
                    final PathMatchingResourcePatternResolver resolver =
                            new PathMatchingResourcePatternResolver(ResourceUtils.class.getClassLoader());
                    Arrays.stream(resolver.getResources(PATTERN))
                            .forEach(file -> Try.of(() -> fixtures.put(file.getFilename(), getFileFromResourceAsStream(file.getFilename())))
                            .onFailure((ex) -> log.error("Unable to read resources")));
                }
        ).onFailure((ex) -> log.error("Unable to process fixturees"));
    }

    public static String getFixture(final String filename) {
        return fixtures.get(filename + EXTENSION);
    }

    private static String getFileFromResourceAsStream(final String filename) throws IOException {
        final InputStream inputStream = ResourceUtils.class.getClassLoader().getResourceAsStream(PATH + filename);

        if (inputStream == null) {
            log.error("File not found: '{}'", filename);
            throw new IllegalArgumentException("File does not existQ");
        }

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
