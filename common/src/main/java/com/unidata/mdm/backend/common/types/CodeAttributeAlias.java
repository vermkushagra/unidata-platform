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

package com.unidata.mdm.backend.common.types;

/**
 * @author Mikhail Mikhailov
 * Alias of a code attribute.
 */
public class CodeAttributeAlias {

    /**
     * Lookup attribute name, which is an alternative of the code attribute (alias of the code attribute).
     */
    private final String aliasAttributeName;
    /**
     * Name of the record attribute, containing alias value.
     */
    private final String recordAttributeName;
    /**
     * Constructor.
     */
    public CodeAttributeAlias(String aliasAttributeName, String recordAttributeName) {
        super();
        this.aliasAttributeName = aliasAttributeName;
        this.recordAttributeName = recordAttributeName;
    }
    /**
     * @return the aliasAttributeName
     */
    public String getAliasAttributeName() {
        return aliasAttributeName;
    }
    /**
     * @return the recordAttributeName
     */
    public String getRecordAttributeName() {
        return recordAttributeName;
    }
}
