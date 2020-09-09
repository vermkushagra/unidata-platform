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

package com.unidata.mdm.backend.service.classifier.po;

/**
 * The Class ClsfNodeSimpleAttrPO.
 */
public class ClsfNodeSimpleAttrPO extends ClsfNodeAttrPO {

    /** The enum data type. */
    private String enumDataType;

    /** The default value. */
    private String defaultValue;

    /**
     * The Enum FieldColumns.
     */
    public enum FieldColumns {

        /** The enum data type. */
        ENUM_DATA_TYPE,

        /** The default value. */
        DEFAULT_VALUE
    }

    /**
     * Gets the enum data type.
     *
     * @return the enum data type
     */
    public String getEnumDataType() {
        return enumDataType;
    }

    /**
     * Sets the enum data type.
     *
     * @param enumDataType the new enum data type
     */
    public void setEnumDataType(String enumDataType) {
        this.enumDataType = enumDataType;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue
     *            the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
