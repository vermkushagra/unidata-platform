package org.unidata.mdm.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class IOUtils {
    private IOUtils() { }

    private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);

    public static String readFromClasspath(String path) {
        try (final InputStream stream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return null;
            }
            return org.apache.commons.io.IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Error happened while reading {}", path, e);
            return null;
        }
    }
}
