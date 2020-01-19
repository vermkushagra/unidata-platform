/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
