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

package com.unidata.mdm.backend.service.dump.protostuff;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Just a static schemas holder.
 */
public final class Schemas {
    /**
     * Data record schema.
     */
    public static final Schema<DataRecord> DATA_RECORD_SCHEMA = new DataRecordSchema();
    /**
     * Simple attribute schema.
     */
    public static final Schema<SimpleAttribute<?>> SIMPLE_ATTRIBUTE_SCHEMA = new SimpleAttributeSchema();
    /**
     * Code attribute schema.
     */
    public static final Schema<CodeAttribute<?>> CODE_ATTRIBUTE_SCHEMA = new CodeAttributeSchema();
    /**
     * Array attribute schema.
     */
    public static final Schema<ArrayAttribute<?>> ARRAY_ATTRIBUTE_SCHEMA = new ArrayAttributeSchema();
    /**
     * Complex attribute schema.
     */
    public static final Schema<ComplexAttribute> COMPLEX_ATTRIBUTE_SCHEMA = new ComplexAttributeSchema();
    /**
     * Constructor.
     */
    private Schemas() {
        super();
    }
}
