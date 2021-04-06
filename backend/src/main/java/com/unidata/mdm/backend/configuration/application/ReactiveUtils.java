package com.unidata.mdm.backend.configuration.application;

import java.util.function.Consumer;

import org.slf4j.Logger;

public class ReactiveUtils {
    public static final <T> Consumer<T> errorLogAndSkipConsumer(Consumer<T> consumer, Logger logger) {
        return (value) -> {
            try {
                consumer.accept(value);
            }
            catch (Exception e) {
                logger.error("Error on consume value: {}", value, e);
            }
        };
    }
}
