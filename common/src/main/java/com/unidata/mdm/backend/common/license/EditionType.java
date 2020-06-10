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
 * Edition type. Platform features turned on or off depending on the type.
 */
public enum EditionType {
    /**
     * Standard edition.
     */
    STANDARD_EDITION("standard", 1 << 0),
    /**
     * High performance edition.
     */
    HP_EDITION("hpe", 1 << 1);
    /**
     * Safe create from tag value.
     * @param val the tag value
     * @return enum or null
     */
    public static EditionType ofTagValue(String val) {

        for (int i = 0; i < values().length; i++) {
            if (values()[i].tag().equals(val)) {
                return values()[i];
            }
        }

        return null;
    }
    /**
     * Constructor.
     * @param tag this edition type configuration tag
     */
    private EditionType(String tag, int mark) {
        this.tag = tag;
        this.mask = mark;
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
        return STANDARD_EDITION.mask() | HP_EDITION.mask();
    }
}
