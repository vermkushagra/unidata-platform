package org.unidata.mdm.core.util;

import java.util.function.Consumer;

import org.slf4j.Logger;

public final class ReactiveUtils {
    private ReactiveUtils() {}

    public static <T> Consumer<T> errorLogAndSkipConsumer(Consumer<T> consumer, Logger logger) {
        return value -> {
            try {
                consumer.accept(value);
            }
            catch (Exception e) {
                logger.error("Error on consume value: {}", value, e);
            }
        };
    }
}
