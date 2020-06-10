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

package com.unidata.mdm.backend.common.license;

/**
 * @author Mikhail Mikhailov
 * Platform operation mode, according to the license.
 */
public enum OperationMode {
    /**
     * Production mode. Default, if nothing specified.
     */
    PRODUCTION_MODE("production", 1 << 0),
    /**
     * Development mode. Some features, not visible otherwise, will be available.
     */
    DEVELOPMENT_MODE("development", 1 << 1);
    /**
     * Safe create from tag value.
     * @param val the tag value
     * @return enum or null
     */
    public static OperationMode ofTagValue(String val) {

        for (int i = 0; i < values().length; i++) {
            if (values()[i].tag().equals(val)) {
                return values()[i];
            }
        }

        return null;
    }
    /**
     * Constructor.
     * @param tag this mode configuration tag
     */
    private OperationMode(String tag, int mask) {
        this.tag = tag;
        this.mask = mask;
    }
    /**
     * Config. tag value.
     */
    private final String tag;
    /**
     * Bit mask.
     */
    private final int mask;
    /**
     * @return the configuration tag
     */
    public String tag() {
        return tag;
    }
    /**
     * @return the mask
     */
    public int mask() {
        return mask;
    }
    /**
     * Mask all.
     * @return all mask
     */
    public static int maskAll() {
        return PRODUCTION_MODE.mask() | DEVELOPMENT_MODE.mask();
    }
}
